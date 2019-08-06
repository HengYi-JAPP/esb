package com.hengyi.japp.esb.core;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * keytool -genkey -keystore esb-auth.jceks -storetype jceks -storepass esb-auth-tomking -keyalg RSA -keysize 2048 -alias RS512 -keypass esb-auth-tomking -sigalg SHA512withRSA -dname "CN=esb, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-18
 */
@Slf4j
public abstract class MainVerticle extends AbstractVerticle {

    protected static DeploymentOptions deploymentOptions(String path) throws Exception {
        final File file = FileUtils.getFile(path, "config.json");
        final Map map = MAPPER.readValue(file, Map.class);
        final JsonObject config = new JsonObject(map);
        return new DeploymentOptions().setConfig(config);
    }

    protected static Vertx vertx() {
        final VertxOptions vertxOptions = new VertxOptions()
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS);
        return Vertx.vertx(vertxOptions);
    }

}
