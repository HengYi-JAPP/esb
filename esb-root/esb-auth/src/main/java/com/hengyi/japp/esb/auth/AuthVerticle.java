package com.hengyi.japp.esb.auth;

import com.hengyi.japp.esb.auth.verticle.AuthAgentVerticle;
import com.hengyi.japp.esb.auth.verticle.AuthServiceVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * keytool -genkey -keystore esb-auth.jceks -storetype jceks -storepass esb-auth-tomking -keyalg RSA -keysize 2048 -alias RS512 -keypass esb-auth-tomking -sigalg SHA512withRSA -dname "CN=esb, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-18
 */
@Slf4j
public class AuthVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        AuthGuiceModule.init(vertx);
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
