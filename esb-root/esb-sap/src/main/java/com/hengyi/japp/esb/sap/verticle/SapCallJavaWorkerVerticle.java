package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.core.apm.RCTextMapExtractAdapter;
import com.hengyi.japp.esb.sap.interfaces.nnfp.NnfpService;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import static com.hengyi.japp.esb.sap.SapVerticle.SAP_INJECTOR;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;

/**
 * 描述：
 *
 * @author jzb 2018-03-18
 */
@Slf4j
public class SapCallJavaWorkerVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                kpOrderSync().rxCompletionHandler(),
                queryElectricKp().rxCompletionHandler()
        );
    }

    private MessageConsumer<String> kpOrderSync() {
        return vertx.eventBus().consumer("esb:sap:NnfpService:kpOrderSync", reply -> {
            final NnfpService nnfpService = SAP_INJECTOR.getInstance(NnfpService.class);
            final String body = reply.body();
            nnfpService.kpOrderSync(body).subscribe(reply::reply, err -> {
                log.error("", err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer<String> queryElectricKp() {
        return vertx.eventBus().consumer("esb:sap:NnfpService:queryElectricKp", reply -> {
            final NnfpService nnfpService = SAP_INJECTOR.getInstance(NnfpService.class);
            final String body = reply.body();
            nnfpService.queryElectricKp(body).subscribe(reply::reply, err -> {
                log.error("", err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private Span initApm(Message<JsonObject> reply, String rfcName, String body) {
        try {
            final Tracer tracer = SAP_INJECTOR.getInstance(Tracer.class);
            final SpanContext spanContext = tracer.extract(TEXT_MAP, new RCTextMapExtractAdapter(reply));
            final Tracer.SpanBuilder spanBuilder = tracer.buildSpan(rfcName)
                    .withTag(Tags.COMPONENT, JavaCallSapWorkerVerticle.class.getSimpleName())
                    .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CONSUMER)
                    .withTag(Tags.MESSAGE_BUS_DESTINATION, "esb:sap:JavaCallSap");
            if (spanContext != null) {
                spanBuilder.asChildOf(spanContext);
            }
            return spanBuilder.start();
        } catch (Throwable e) {
            log.error("initApm", e);
            return null;
        }
    }

    private void apmSuccess(String rfcName, Span span, Message<JsonObject> reply, String message) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, false);
        span.log(message);
        span.finish();
    }

    private void apmError(String rfcName, Span span, Throwable err) {
        log.error(rfcName, err);
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, true);
        span.log(err.getLocalizedMessage());
        span.finish();
    }

}
