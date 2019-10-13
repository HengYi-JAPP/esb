package com.hengyi.japp.esb.sap;

import com.hengyi.japp.esb.sap.verticle.JavaCallSapAgentVerticle;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapWorkerAsyncVerticle;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapWorkerVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class SapVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        SapGuiceModule.init(vertx);
        CompositeFuture.all(
                deployJavaCallSapWorker(),
                deployJavaCallSapWorkerAsync()
        ).compose(f -> deployJavaCallSapAgent()).<Void>mapEmpty().setHandler(startFuture);
    }

    private Future<String> deployJavaCallSapAgent() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(20);
            vertx.deployVerticle(JavaCallSapAgentVerticle.class, deploymentOptions, promise);
        });
    }

    private Future<String> deployJavaCallSapWorker() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setConfig(config())
                    .setWorker(true)
                    .setInstances(10_000)
                    .setMaxWorkerExecuteTime(1)
                    .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS);
            vertx.deployVerticle(JavaCallSapWorkerVerticle.class, deploymentOptions, promise);
        });
    }

    private Future<String> deployJavaCallSapWorkerAsync() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setConfig(config())
                    .setWorker(true);
            vertx.deployVerticle(JavaCallSapWorkerAsyncVerticle.class, deploymentOptions, promise);
        });
    }

//    private Single<String> deploySapCallJavaAgent() {
//        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config());
//        return vertx.rxDeployVerticle(SapCallJavaAgentVerticle.class.getName(), deploymentOptions);
//    }
//
//    private Single<String> deploySapCallJavaWorker() {
//        final DeploymentOptions deploymentOptions = new DeploymentOptions()
//                .setConfig(config())
//                .setWorker(true)
//                .setMaxWorkerExecuteTime(1)
//                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
//                .setInstances(1000);
//        return vertx.rxDeployVerticle(SapCallJavaWorkerVerticle.class.getName(), deploymentOptions);
//    }

}
