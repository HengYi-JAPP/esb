package com.hengyi.japp.esb.sap.verticle;

import com.google.common.collect.Maps;
import com.hengyi.japp.esb.core.apm.RCTextMapExtractAdapter;
import com.hengyi.japp.esb.sap.SapUtil;
import com.hengyi.japp.esb.sap.apm.RCTextMapInjectAdapter_OutboundMessage;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.ExceptionHandlers;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.SendOptions;
import reactor.rabbitmq.Sender;

import java.time.Duration;
import java.util.Map;

import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.sap.SapVerticle.SAP_INJECTOR;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class JavaCallSapWorkerVerticle extends AbstractVerticle {
    public static final String QUEUE_SAP = "esb-sap-queue";
    private static final SendOptions sendOptions = new SendOptions().exceptionHandler(
            new ExceptionHandlers.RetrySendingExceptionHandler(
                    Duration.ofHours(1), Duration.ofMinutes(5),
                    ExceptionHandlers.CONNECTION_RECOVERY_PREDICATE
            )
    );

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        CompositeFuture.all(
                sync("esb:sap:JavaCallSap"),
                async("esb:sap:async:JavaCallSap")
        ).<Void>mapEmpty().setHandler(startFuture);
    }

    private Future<Void> sync(String address) {
        return Future.future(promise -> vertx.eventBus().<JsonObject>consumer(address, reply -> {
            final JsonObject jsonObject = reply.body();
            final String rfcName = jsonObject.getString("rfcName");
            final StringBuilder sb = new StringBuilder(rfcName).append("\n");
            final String body = jsonObject.getString("body");
            final Tracer tracer = SAP_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, rfcName, address);
            Mono.fromCallable(() -> {
                final JCoDestination dest = JCoDestinationManager.getDestination(JcoDataProvider.KEY);
                final JCoFunction f = dest.getRepository().getFunctionTemplate(rfcName).getFunction();
                SapUtil.setParam(f, body);
                f.execute(dest);
                return SapUtil.params2String(f);
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                log.info(sb.append(it).toString());
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                log.error(rfcName, err);
                reply.fail(400, err.getMessage());
            });
        }).completionHandler(promise));
    }

    private Future<Void> async(String address) {
        return Future.future(promise -> vertx.eventBus().<JsonObject>consumer(address, reply -> {
            final JsonObject jsonObject = reply.body();
            final Tracer tracer = SAP_INJECTOR.getInstance(Tracer.class);
            final SpanContext spanContext = tracer.extract(TEXT_MAP, new RCTextMapExtractAdapter(reply));
            final Map<String, Object> headers = Maps.newHashMap();
            tracer.inject(spanContext, TEXT_MAP, new RCTextMapInjectAdapter_OutboundMessage(headers));
            final BasicProperties basicProperties = new BasicProperties.Builder().headers(headers).build();
            final OutboundMessage outboundMessage = new OutboundMessage("", QUEUE_SAP, basicProperties, jsonObject.encode().getBytes(UTF_8));
            final Mono<OutboundMessage> outboundMessageMono = Mono.just(outboundMessage);
            final Sender sender = SAP_INJECTOR.getInstance(Sender.class);
            sender.sendWithPublishConfirms(outboundMessageMono, sendOptions).subscribe(ret -> {
                if (ret.isAck()) {
                    reply.reply(null);
                } else {
                    log.error(new StringBuilder("ack=fail").append("\n").append(jsonObject.encode()).toString());
                    reply.fail(400, "ack=fail");
                }
            }, err -> reply.fail(400, err.getMessage()));
        }).completionHandler(promise));
    }

}
