package com.hengyi.japp.esb.sap.verticle;

import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述：
 *
 * @author jzb 2018-03-18
 */
@Slf4j
public class SapCallJavaWorkerVerticle extends AbstractVerticle {

//    @Override
//    public Completable rxStart() {
//        return Completable.mergeArray(
//                kpOrderSync().rxCompletionHandler(),
//                queryElectricKp().rxCompletionHandler()
//        );
//    }
//
//    private MessageConsumer<String> kpOrderSync() {
//        return vertx.eventBus().consumer("esb:sap:NnfpService:kpOrderSync", reply -> {
//            final NnfpService nnfpService = SAP_INJECTOR.getInstance(NnfpService.class);
//            final String body = reply.body();
//            nnfpService.kpOrderSync(body).subscribe(reply::reply, err -> {
//                log.error("", err);
//                reply.fail(400, err.getLocalizedMessage());
//            });
//        });
//    }
//
//    private MessageConsumer<String> queryElectricKp() {
//        return vertx.eventBus().consumer("esb:sap:NnfpService:queryElectricKp", reply -> {
//            final NnfpService nnfpService = SAP_INJECTOR.getInstance(NnfpService.class);
//            final String body = reply.body();
//            nnfpService.queryElectricKp(body).subscribe(reply::reply, err -> {
//                log.error("", err);
//                reply.fail(400, err.getLocalizedMessage());
//            });
//        });
//    }

}
