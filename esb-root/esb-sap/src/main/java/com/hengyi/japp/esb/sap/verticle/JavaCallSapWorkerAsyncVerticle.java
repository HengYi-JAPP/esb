package com.hengyi.japp.esb.sap.verticle;

import com.github.ixtf.japp.core.J;
import com.google.common.collect.Maps;
import com.hengyi.japp.esb.sap.SapUtil;
import com.hengyi.japp.esb.sap.apm.RCTextMapExtractAdapter_OutboundMessage;
import com.hengyi.japp.esb.sap.apm.RCTextMapInjectAdapter_OutboundMessage;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Delivery;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import java.time.Duration;
import java.util.Map;

import static com.hengyi.japp.esb.core.Util.apmError;
import static com.hengyi.japp.esb.core.Util.apmSuccess;
import static com.hengyi.japp.esb.sap.SapVerticle.SAP_INJECTOR;
import static com.hengyi.japp.esb.sap.verticle.JavaCallSapWorkerVerticle.QUEUE_SAP;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class JavaCallSapWorkerAsyncVerticle extends AbstractVerticle {
    public static final String EXCHANGE_CALLBACK = "esb-sap-callback-exchange";
    private static final SendOptions sendOptions = new SendOptions().exceptionHandler(
            new ExceptionHandlers.RetrySendingExceptionHandler(
                    Duration.ofHours(1), Duration.ofMinutes(5),
                    ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE
            )
    );
    private static final ConsumeOptions consumeOptions = new ConsumeOptions().exceptionHandler(
            new ExceptionHandlers.RetryAcknowledgmentExceptionHandler(
                    Duration.ofMinutes(10), Duration.ofSeconds(5),
                    ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE
            )
    );

    public static Span initApm(Delivery delivery, Tracer tracer, Object component, String operationName) {
        try {
            final SpanContext spanContext = tracer.extract(TEXT_MAP, new RCTextMapExtractAdapter_OutboundMessage(delivery));
            final Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName)
                    .withTag(Tags.COMPONENT, component.getClass().getName())
                    .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CONSUMER)
                    .withTag(Tags.MESSAGE_BUS_DESTINATION, EXCHANGE_CALLBACK);
            if (spanContext != null) {
                spanBuilder.asChildOf(spanContext);
            }
            return spanBuilder.start();
        } catch (Throwable e) {
            log.error("initApm", e);
            return null;
        }
    }

    @Override
    public void start() throws Exception {
        final Receiver receiver = SAP_INJECTOR.getInstance(Receiver.class);
        receiver.consumeManualAck(QUEUE_SAP, consumeOptions).subscribe(this::callSap);
    }

    private void callSap(AcknowledgableDelivery delivery) {
        final JsonObject jsonObject = new JsonObject(Buffer.buffer(delivery.getBody()));
        final String rfcName = jsonObject.getString("rfcName");
        final StringBuilder sb = new StringBuilder(rfcName).append("\n");
        final String body = jsonObject.getString("body");
        final Tracer tracer = SAP_INJECTOR.getInstance(Tracer.class);
        final Span span = initApm(delivery, tracer, this, rfcName);
        Mono.fromCallable(() -> {
            final JCoDestination dest = JCoDestinationManager.getDestination(JcoDataProvider.KEY);
            final JCoFunction f = dest.getRepository().getFunctionTemplate(rfcName).getFunction();
            SapUtil.setParam(f, body);
            f.execute(dest);
            return SapUtil.params2String(f);
        }).subscribeOn(Schedulers.elastic()).doOnSuccess(sapRet -> {
            delivery.ack();
            final Map<String, Object> headers = Maps.newHashMap();
            tracer.inject(span.context(), TEXT_MAP, new RCTextMapInjectAdapter_OutboundMessage(headers));
            final BasicProperties basicProperties = new BasicProperties.Builder().headers(headers).build();
            final OutboundMessage outboundMessage = new OutboundMessage(EXCHANGE_CALLBACK, rfcName, basicProperties, sapRet.getBytes(UTF_8));
            final Mono<OutboundMessage> outboundMessageMono = Mono.just(outboundMessage);
            final Sender sender = SAP_INJECTOR.getInstance(Sender.class);
            sender.sendWithPublishConfirms(outboundMessageMono, sendOptions).next().subscribe(it -> {
                if (it.isAck()) {
                    apmSuccess(span, sapRet);
                    log.info(sb.append(sapRet).toString());
                } else {
                    amqpError(span, sapRet, new RuntimeException("ack=false"));
                    log.error(sb.append(sapRet).append("ack=false").toString());
                }
            }, err -> {
                amqpError(span, sapRet, err);
                log.error(sb.append(sapRet).toString(), err);
            });
        }).doOnError(err -> {
            apmError(span, err);
            log.error(rfcName, err);
        }).subscribe();
    }

    private void amqpError(Span span, String message, Throwable err) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, true);
        if (J.nonBlank(message)) {
            span.log(message);
        }
        span.log(err.getMessage());
        span.finish();
    }

}
