package com.hengyi.japp.esb.sap.callback.verticle;

import com.fasterxml.jackson.databind.JsonNode;
import com.hengyi.japp.esb.sap.callback.apm.RCTextMapExtractAdapter_OutboundMessage;
import com.rabbitmq.client.Delivery;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.vertx.core.AbstractVerticle;
import loa.biz.LOAApp;
import loa.biz.LOAFormDataObject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.AcknowledgableDelivery;
import reactor.rabbitmq.ConsumeOptions;
import reactor.rabbitmq.ExceptionHandlers;
import reactor.rabbitmq.Receiver;

import java.time.Duration;
import java.util.UUID;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.hengyi.japp.esb.core.Util.apmError;
import static com.hengyi.japp.esb.core.Util.apmSuccess;
import static com.hengyi.japp.esb.sap.callback.LoaVerticle.LOA_INJECTOR;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;

/**
 * @author jzb 2019-10-10
 */
@Slf4j
public class ZRFC_SD_YX_003_Verticle extends AbstractVerticle {
    private static final String RFC_NAME = "ZRFC_SD_YX_003";
    private static final String QUEUE = "esb-sap-callback-" + RFC_NAME;
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
                    .withTag(Tags.MESSAGE_BUS_DESTINATION, QUEUE);
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
        final Receiver receiver = LOA_INJECTOR.getInstance(Receiver.class);
        receiver.consumeManualAck(QUEUE, consumeOptions).subscribe(this::exe);
    }

    private void exe(AcknowledgableDelivery delivery) {
        final LOAApp app = LOA_INJECTOR.getInstance(LOAApp.class);
        final Tracer tracer = LOA_INJECTOR.getInstance(Tracer.class);
        final Span span = initApm(delivery, tracer, this, RFC_NAME);
        Mono.fromCallable(() -> {
            final JsonNode node = MAPPER.readTree(delivery.getBody());
            final JsonNode ET_ORDER_RE = node.get("tables").get("ET_ORDER_RE");
            final String TRANSID = UUID.randomUUID().toString();
            for (JsonNode row : ET_ORDER_RE) {
                LOAFormDataObject vObj = app.newFormDataObject("ZRFC_SD_YX_003回调表");
                vObj.addRawValue("TRANSID", TRANSID);
                vObj.addRawValue("VBELN", row.get("VBELN").asText());
                vObj.addRawValue("OPT", row.get("OPT").asText());
                vObj.addRawValue("ZYXDH", row.get("ZYXDH").asText());
                vObj.addRawValue("POSNR", row.get("POSNR").asText());
                vObj.addRawValue("ZTZDNO", row.get("ZTZDNO").asText());
                vObj.addRawValue("ZTZDINO", row.get("ZTZDINO").asText());
                vObj.addRawValue("SONUM", row.get("SONUM").asText());
                vObj.addRawValue("SOITEMNUM", row.get("SOITEMNUM").asText());
                vObj.addRawValue("TYPE", row.get("TYPE").asText());
                vObj.addRawValue("ITEMOPT", row.get("ITEMOPT").asText());
                vObj.addRawValue("OPTNUM", row.get("OPTNUM").asText());
                vObj.save();
            }
            return true;
        }).subscribeOn(Schedulers.elastic()).doOnSuccess(b -> {
            if (b) {
                delivery.ack();
                apmSuccess(span, "");
            } else {
                apmError(span, new RuntimeException("LOAApp"));
            }
        }).doOnError(err -> {
            apmError(span, err);
            log.error(RFC_NAME, err);
        }).subscribe();
    }
}
