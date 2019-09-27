package com.hengyi.japp.esb.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.auth.verticle.AuthAgentVerticle;
import com.hengyi.japp.esb.auth.verticle.AuthServiceVerticle;
import com.hengyi.japp.esb.core.GuiceModule;
import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * keytool -genkey -keystore esb-auth.jceks -storetype jceks -storepass esb-auth-tomking -keyalg RSA -keysize 2048 -alias RS512 -keypass esb-auth-tomking -sigalg SHA512withRSA -dname "CN=esb, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-18
 */
@Slf4j
public class AuthVerticle extends AbstractVerticle {
    public static String AUTH_MODULE = "esb-auth";
    public static Injector AUTH_INJECTOR;

    public static void main(String[] args) {
        final VertxOptions vertxOptions = new VertxOptions()
                .setWorkerPoolSize(1000)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
        final Vertx vertx = Vertx.vertx(vertxOptions);
        AUTH_INJECTOR = Guice.createInjector(new GuiceModule(vertx, AUTH_MODULE), new AuthGuiceModule());

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        vertx.deployVerticle(AuthVerticle.class, deploymentOptions, ar -> {
            if (ar.succeeded()) {
                log.info("===Esb Auth[" + ar.result() + "] 启动成功===");
            } else {
                log.error("===Esb Auth 启动失败===", ar.cause());
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        deployAuthService().compose(f -> deployAgent()).<Void>mapEmpty().setHandler(startFuture);
    }

    private Future<String> deployAgent() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(20);
            vertx.deployVerticle(AuthAgentVerticle.class, deploymentOptions, promise);
        });
    }

    private Future<String> deployAuthService() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setWorker(true)
                    .setInstances(10_000)
                    .setMaxWorkerExecuteTime(1)
                    .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS);
            vertx.deployVerticle(AuthServiceVerticle.class, deploymentOptions, promise);
        });
    }

}
