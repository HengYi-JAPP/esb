package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.core.apm.RCTextMapExtractAdapter;
import com.hengyi.japp.esb.sap.SapUtil;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

import static com.hengyi.japp.esb.sap.SapVerticle.SAP_INJECTOR;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class JavaCallSapWorkerVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return vertx.eventBus().<JsonObject>consumer("esb:sap:JavaCallSap", reply -> {
            final JsonObject jsonObject = reply.body();
            final String rfcName = jsonObject.getString("rfcName");
            final String body = jsonObject.getString("body");
            final Span span = initApm(reply, rfcName);
            Single.fromCallable(() -> {
                final JCoDestination dest = JCoDestinationManager.getDestination(JcoDataProvider.KEY);
                final JCoFunction f = dest.getRepository().getFunctionTemplate(rfcName).getFunction();
                SapUtil.setParam(f, body);
                f.execute(dest);
                return SapUtil.params2String(f);
            }).subscribe(it -> {
                apmSuccess(rfcName, span, reply, it);
                reply.reply(it);
            }, err -> {
                apmError(rfcName, span, err);
                reply.fail(400, err.getMessage());
            });
        }).rxCompletionHandler();
    }

    private Span initApm(Message reply, String operationName) {
        try {
            final Tracer tracer = SAP_INJECTOR.getInstance(Tracer.class);
            final SpanContext spanContext = tracer.extract(TEXT_MAP, new RCTextMapExtractAdapter(reply));
            final Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName)
                    .withTag(Tags.COMPONENT, this.getClass().getName())
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

    private void apmSuccess(String rfcName, Span span, Message reply, String message) {
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
