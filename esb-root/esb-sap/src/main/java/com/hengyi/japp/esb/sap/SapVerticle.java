package com.hengyi.japp.esb.sap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.core.MainVerticle;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapAgentVerticle;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapWorkerVerticle;
import com.hengyi.japp.esb.sap.verticle.SapCallJavaAgentVerticle;
import com.hengyi.japp.esb.sap.verticle.SapCallJavaWorkerVerticle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class SapVerticle extends MainVerticle {
    public static Injector SAP_INJECTOR;

    public static void main(String[] args) {
        JcoDataProvider.init("/home/esb/esb-sap");
        Single.fromCallable(() -> deploymentOptions("/home/esb/esb-sap")).flatMap(deploymentOptions -> {
            final Vertx vertx = vertx();
            SAP_INJECTOR = Guice.createInjector(new GuiceModule(vertx), new SapGuiceModule());
            return vertx.rxDeployVerticle(SapVerticle.class.getName(), deploymentOptions);
        }).subscribe(
                it -> log.info("===Esb Sap[" + it + "] 启动成功==="),
                err -> log.error("===Esb Sap 启动失败===", err)
        );
    }

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                deployJavaCallSapAgent().ignoreElement(),
                deployJavaCallSapWorker().ignoreElement()
//                deploySapCallJavaAgent().ignoreElement(),
//                deploySapCallJavaWorker().ignoreElement()
        );
    }

    private Single<String> deployJavaCallSapAgent() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config()).setInstances(20);
        return vertx.rxDeployVerticle(JavaCallSapAgentVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployJavaCallSapWorker() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(JavaCallSapWorkerVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deploySapCallJavaAgent() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config());
        return vertx.rxDeployVerticle(SapCallJavaAgentVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deploySapCallJavaWorker() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(SapCallJavaWorkerVerticle.class.getName(), deploymentOptions);
    }

}
