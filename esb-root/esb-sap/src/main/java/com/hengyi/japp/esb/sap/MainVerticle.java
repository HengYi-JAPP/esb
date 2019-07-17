package com.hengyi.japp.esb.sap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.core.verticle.BaseEsbVerticle;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapAgent;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapWorker;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-03-18
 */
public class MainVerticle extends BaseEsbVerticle {
    public static Injector GUICE;

    @Override
    public void start(Future<Void> startFuture) {
        JcoDataProvider.init(Util.readProperties(config().getString("rootPath"), "sap.properties"));
        GUICE = Guice.createInjector(new GuiceSapModule(vertx));

        Completable.mergeArray(
                deployJavaCallSapAgent().ignoreElement(),
                deployJavaCallSapWorker().ignoreElement()
//                vertx.rxDeployVerticle(SapCallJavaVerticle.class.getName(), deploymentOptions).toCompletable()
        ).subscribe(() -> {
            startFuture.complete();
            log.info("===Esb Sap 启动成功===");
        }, ex -> {
            startFuture.fail(ex);
            log.error("===Esb Sap 启动失败===", ex);
        });
    }

    private Single<String> deployJavaCallSapAgent() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setInstances(20);
        return vertx.rxDeployVerticle(JavaCallSapAgent.class.getName(), deploymentOptions);
    }

    private Single<String> deployJavaCallSapWorker() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setWorker(true)
                .setConfig(config())
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(JavaCallSapWorker.class.getName(), deploymentOptions);
    }

    @Override
    public String getAppid() {
        return "ce034307-2ad9-11e8-ab84-87fa685516db";
    }

    @Override
    public String getAppsecret() {
        // TODO getAppsecret
        return null;
    }
}
