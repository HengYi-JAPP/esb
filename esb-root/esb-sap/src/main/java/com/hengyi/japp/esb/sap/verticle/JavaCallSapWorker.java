package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.sap.SapUtil;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class JavaCallSapWorker extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return vertx.eventBus().<JsonObject>consumer("esb:sap:JavaCallSap", reply -> {
            final JsonObject jsonObject = reply.body();
            final StringBuilder sb = new StringBuilder(jsonObject.encode()).append("\n");
            final String rfcName = jsonObject.getString("rfcName");
            final String body = jsonObject.getString("body");
            Single.fromCallable(() -> {
                final JCoDestination dest = JCoDestinationManager.getDestination(JcoDataProvider.KEY);
                final JCoFunction f = dest.getRepository().getFunctionTemplate(rfcName).getFunction();
                SapUtil.setParam(f, body);
                f.execute(dest);
                return SapUtil.params2String(f);
            }).subscribe(it -> {
                reply.reply(it);
                log.info(sb.append(it).toString());
            }, err -> {
                reply.fail(400, err.getLocalizedMessage());
                log.error(sb.toString(), err);
            });
        }).rxCompletionHandler();
    }

}
