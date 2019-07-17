package com.hengyi.japp.esb.auth;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.auth.verticle.AuthVerticle;
import com.hengyi.japp.esb.core.verticle.BaseEsbVerticle;
import io.reactivex.Completable;
import io.vertx.core.DeploymentOptions;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * keytool -genkey -keystore esb-auth.jceks -storetype jceks -storepass esb-auth-tomking -keyalg RSA -keysize 2048 -alias RS512 -keypass esb-auth-tomking -sigalg SHA512withRSA -dname "CN=esb, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-18
 */
public class MainVerticle extends BaseEsbVerticle {
    public static Injector GUICE;

    @Override
    public Completable rxStart() {
        GUICE = Guice.createInjector(new GuiceAuthModule(vertx));

        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config());
        return Completable.mergeArray(
                vertx.rxDeployVerticle(AuthVerticle.class.getName(), deploymentOptions).ignoreElement()
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
