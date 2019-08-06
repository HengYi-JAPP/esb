package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.sap.SapUtil;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.sap.SapVerticle.SAP_INJECTOR;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class JavaCallSapWorkerVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        final String address = "esb:sap:JavaCallSap";
        return vertx.eventBus().<JsonObject>consumer(address, reply -> {
            final JsonObject jsonObject = reply.body();
            final String rfcName = jsonObject.getString("rfcName");
            final String body = jsonObject.getString("body");
            final Tracer tracer = SAP_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, rfcName, address);
            Single.fromCallable(() -> {
                final JCoDestination dest = JCoDestinationManager.getDestination(JcoDataProvider.KEY);
                final JCoFunction f = dest.getRepository().getFunctionTemplate(rfcName).getFunction();
                SapUtil.setParam(f, body);
                f.execute(dest);
                return SapUtil.params2String(f);
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                reply.reply(it);
            }, err -> {
                log.error(rfcName, err);
                apmError(reply, span, err);
                reply.fail(400, err.getMessage());
            });
        }).rxCompletionHandler();
    }

}
