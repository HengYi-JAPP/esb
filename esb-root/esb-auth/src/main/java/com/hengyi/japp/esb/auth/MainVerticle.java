package com.hengyi.japp.esb.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.auth.verticle.AuthWorkerVerticle;
import com.hengyi.japp.esb.core.GuiceModule;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.AbstractVerticle;

/**
 * keytool -genkey -keystore esb-auth.jceks -storetype jceks -storepass esb-auth-tomking -keyalg RSA -keysize 2048 -alias RS512 -keypass esb-auth-tomking -sigalg SHA512withRSA -dname "CN=esb, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-18
 */
public class MainVerticle extends AbstractVerticle {
    public static Injector INJECTOR;

    @Override
    public Completable rxStart() {
        INJECTOR = Guice.createInjector(new GuiceModule(vertx), new GuiceAuthModule());

        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config());
        return Completable.mergeArray(
                vertx.rxDeployVerticle(AuthWorkerVerticle.class.getName(), deploymentOptions).ignoreElement()
        );
    }

}
