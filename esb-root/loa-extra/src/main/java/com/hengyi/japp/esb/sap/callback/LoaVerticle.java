package com.hengyi.japp.esb.sap.callback;

import com.hengyi.japp.esb.sap.callback.verticle.ZRFC_SD_YX_003_Verticle;
import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class LoaVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        final VertxOptions vertxOptions = new VertxOptions()
//                .setWorkerPoolSize(10_000)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
        final Vertx vertx = Vertx.vertx(vertxOptions);
        LoaGuiceModule.init(vertx);

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        vertx.deployVerticle(LoaVerticle.class, deploymentOptions, ar -> {
            if (ar.succeeded()) {
                log.info("===Esb Loa[" + ar.result() + "] 启动成功===");
            } else {
                log.error("===Esb Loa 启动失败===", ar.cause());
            }
        });
    }

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
