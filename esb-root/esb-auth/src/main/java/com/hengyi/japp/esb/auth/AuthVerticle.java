package com.hengyi.japp.esb.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.auth.verticle.AuthAgentVerticle;
import com.hengyi.japp.esb.auth.verticle.AuthServiceVerticle;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.core.MainVerticle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * keytool -genkey -keystore esb-auth.jceks -storetype jceks -storepass esb-auth-tomking -keyalg RSA -keysize 2048 -alias RS512 -keypass esb-auth-tomking -sigalg SHA512withRSA -dname "CN=esb, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-18
 */
@Slf4j
public class AuthVerticle extends MainVerticle {
    public static Injector AUTH_INJECTOR;

    public static void main(String[] args) {
        Single.fromCallable(() -> deploymentOptions("/home/esb/esb-auth")).flatMap(deploymentOptions -> {
            final Vertx vertx = vertx();
            AUTH_INJECTOR = Guice.createInjector(new GuiceModule(vertx), new AuthGuiceModule());
            return vertx.rxDeployVerticle(AuthVerticle.class.getName(), deploymentOptions);
        }).subscribe(
                it -> log.info("===Esb Auth[" + it + "] 启动成功==="),
                err -> log.error("===Esb Auth 启动失败===", err)
        );
    }

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                deployAuthAgent().ignoreElement(),
                deployAuthService().ignoreElement()
        );
    }

    private Single<String> deployAuthAgent() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config());
        return vertx.rxDeployVerticle(AuthAgentVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployAuthService() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setInstances(1000);
        return vertx.rxDeployVerticle(AuthServiceVerticle.class.getName(), deploymentOptions);
    }

}
