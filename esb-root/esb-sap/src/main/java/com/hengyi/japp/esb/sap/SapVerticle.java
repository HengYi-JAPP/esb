package com.hengyi.japp.esb.sap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapAgentVerticle;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapWorkerAsyncVerticle;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapWorkerVerticle;
import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class SapVerticle extends AbstractVerticle {
    public static String SAP_MODULE = "esb-sap";
    public static Injector SAP_INJECTOR;

    public static void main(String[] args) {
        final VertxOptions vertxOptions = new VertxOptions()
//                .setWorkerPoolSize(10_000)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
        final Vertx vertx = Vertx.vertx(vertxOptions);
        SAP_INJECTOR = Guice.createInjector(new GuiceModule(vertx, SAP_MODULE), new SapGuiceModule());
        JcoDataProvider.init();

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        vertx.deployVerticle(SapVerticle.class, deploymentOptions, ar -> {
            if (ar.succeeded()) {
                log.info("===Esb Sap[" + ar.result() + "] 启动成功===");
            } else {
                log.error("===Esb Sap 启动失败===", ar.cause());
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
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
