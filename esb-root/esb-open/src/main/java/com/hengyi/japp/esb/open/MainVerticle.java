package com.hengyi.japp.esb.open;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.verticle.BaseVerticle;
import com.hengyi.japp.esb.open.verticle.OpenVerticle;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

/**
 * @author jzb 2018-03-18
 */
public class MainVerticle extends BaseVerticle {
    public static Injector GUICE;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        GUICE = Guice.createInjector(new GuiceOpenModule(vertx));

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(config());
        Completable.mergeArray(
                vertx.rxDeployVerticle(OpenVerticle.class.getName(), deploymentOptions).toCompletable()
        ).subscribe(
                () -> {
                    startFuture.complete();
                    log.info("===Esb Open 启动成功===");
                },
                ex -> {
                    startFuture.fail(ex);
                    log.error("===Esb Open 启动失败===", ex);
                }
        );
    }

}
