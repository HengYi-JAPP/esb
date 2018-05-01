package com.hengyi.japp.esb.sms;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.verticle.BaseEsbVerticle;
import com.hengyi.japp.esb.sms.verticle.SmsVerticle;
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
    public void start(Future<Void> startFuture) {
        GUICE = Guice.createInjector(new GuiceSmsModule(vertx));

        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config());
        Completable.mergeArray(
                vertx.rxDeployVerticle(SmsVerticle.class.getName(), deploymentOptions).toCompletable()
        ).subscribe(
                () -> {
                    startFuture.complete();
                    log.info("===Esb Sms 启动成功===");
                },
                ex -> {
                    startFuture.fail(ex);
                    log.error("===Esb Sms 启动失败===", ex);
                }
        );
    }

    @Override
    public String getAppid() {
        return UUID.nameUUIDFromBytes("japp-esb-sms".getBytes(UTF_8)).toString();
    }

    @Override
    public String getAppsecret() {
        return null;
    }
}
