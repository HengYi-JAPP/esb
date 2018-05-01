package com.hengyi.japp.esb.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.auth.verticle.AuthVerticle;
import com.hengyi.japp.esb.core.verticle.BaseEsbVerticle;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.reactivex.ext.dropwizard.MetricsService;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * keytool -genkey -keystore esb-auth.jceks -storetype jceks -storepass esb-auth-tomking -keyalg RSA -keysize 2048 -alias RS512 -keypass esb-auth-tomking -sigalg SHA512withRSA -dname "CN=esb, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-18
 */
public class MainVerticle extends BaseEsbVerticle {
    public static Injector GUICE;
    public static MetricsService metricsService;

    @Override
    public void start(Future<Void> startFuture) {
        GUICE = Guice.createInjector(new GuiceAuthModule(vertx));
        metricsService = MetricsService.create(vertx);

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(config());
        Completable.mergeArray(
                vertx.rxDeployVerticle(AuthVerticle.class.getName(), deploymentOptions).toCompletable()
        ).subscribe(
                () -> {
                    startFuture.complete();
                    log.info("===Esb Auth 启动成功===");
                },
                ex -> {
                    startFuture.fail(ex);
                    log.error("===Esb Auth 启动失败===", ex);
                }
        );
    }

    @Override
    public String getAppid() {
        return UUID.nameUUIDFromBytes("japp-esb-auth".getBytes(UTF_8)).toString();
    }

    @Override
    public String getAppsecret() {
        // TODO getAppsecret
        return null;
    }
}