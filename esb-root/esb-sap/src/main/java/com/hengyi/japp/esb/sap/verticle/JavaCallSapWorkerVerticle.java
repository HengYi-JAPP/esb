package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.sap.SapUtil;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.sap.SapVerticle.SAP_INJECTOR;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class JavaCallSapWorkerVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final String address = "esb:sap:JavaCallSap";
        vertx.eventBus().<JsonObject>consumer(address, reply -> {
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
                log.error(rfcName, err);
                apmError(reply, span, err);
                reply.fail(400, err.getMessage());
            });
        }).completionHandler(startFuture);
    }

}
