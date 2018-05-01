package com.hengyi.japp.esb.sap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.core.verticle.BaseEsbVerticle;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapVerticle;
import com.hengyi.japp.esb.sap.verticle.SapCallJavaVerticle;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

/**
 * @author jzb 2018-03-18
 */
public class MainVerticle extends BaseEsbVerticle {
    public static Injector GUICE;

    @Override
    public void start(Future<Void> startFuture) {
        JcoDataProvider.init(Util.readProperties(config().getString("rootPath"), "sap.properties"));
        GUICE = Guice.createInjector(new GuiceSapModule(vertx));

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(config());
        Completable.mergeArray(
                vertx.rxDeployVerticle(JavaCallSapVerticle.class.getName(), deploymentOptions).toCompletable(),
                vertx.rxDeployVerticle(SapCallJavaVerticle.class.getName(), deploymentOptions).toCompletable()
        ).subscribe(
                () -> {
                    startFuture.complete();
                    log.info("===Esb Sap 启动成功===");
                },
                ex -> {
                    startFuture.fail(ex);
                    log.error("===Esb Sap 启动失败===", ex);
                }
        );
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
