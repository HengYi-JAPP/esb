package com.hengyi.japp.esb.weixin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

/**
 * @author jzb 2018-03-18
 */
public class MainVerticle extends AbstractVerticle {
    public static Injector GUICE;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        GUICE = Guice.createInjector(new GuiceWeixinModule(vertx));

        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config());
//        Completable.mergeArray(
//                vertx.rxDeployVerticle(WeixinVerticle.class.getName(), deploymentOptions).toCompletable()
//        ).subscribe(
//                () -> {
//                    startFuture.complete();
//                    log.info("===Esb Weixin 启动成功===");
//                },
//                ex -> {
//                    startFuture.fail(ex);
//                    log.error("===Esb Weixin 启动失败===", ex);
//                }
//        );
    }
}
