package com.hengyi.japp.esb.weixin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.verticle.BaseEsbVerticle;
import com.hengyi.japp.esb.weixin.verticle.WeixinVerticle;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author jzb 2018-03-18
 */
public class MainVerticle extends BaseEsbVerticle {
    public static Injector GUICE;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        GUICE = Guice.createInjector(new GuiceWeixinModule(vertx));

        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config());
        Completable.mergeArray(
                vertx.rxDeployVerticle(WeixinVerticle.class.getName(), deploymentOptions).toCompletable()
        ).subscribe(
                () -> {
                    startFuture.complete();
                    log.info("===Esb Weixin 启动成功===");
                },
                ex -> {
                    startFuture.fail(ex);
                    log.error("===Esb Weixin 启动失败===", ex);
                }
        );
    }

    @Override
    public String getAppid() {
        return UUID.nameUUIDFromBytes("japp-esb-weixin".getBytes(UTF_8)).toString();
    }

    @Override
    public String getAppsecret() {
        return null;
    }
}
