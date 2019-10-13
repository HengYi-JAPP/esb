package com.hengyi.japp.esb.sap.callback;

import com.hengyi.japp.esb.sap.callback.verticle.ZRFC_SD_YX_003_Verticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class LoaVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        LoaGuiceModule.init(vertx);
        deployZRFC_SD_YX_003().<Void>mapEmpty().setHandler(startFuture);
    }

    private Future<String> deployZRFC_SD_YX_003() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config()).setWorker(true);
            vertx.deployVerticle(ZRFC_SD_YX_003_Verticle.class, deploymentOptions, promise);
        });
    }

}
