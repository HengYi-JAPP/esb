package com.hengyi.japp.esb.sap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapAgent;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapWorker;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.AbstractVerticle;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-03-18
 */
public class MainVerticle extends AbstractVerticle {
    public static Injector GUICE;

    @Override
    public Completable rxStart() {
        JcoDataProvider.init(Util.readProperties(config().getString("rootPath"), "sap.properties"));
        GUICE = Guice.createInjector(new GuiceSapModule(vertx));

        return Completable.mergeArray(
                deployJavaCallSapAgent().ignoreElement(),
                deployJavaCallSapWorker().ignoreElement()
                //                vertx.rxDeployVerticle(SapCallJavaVerticle.class.getName(), deploymentOptions).toCompletable()
        );
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

}
