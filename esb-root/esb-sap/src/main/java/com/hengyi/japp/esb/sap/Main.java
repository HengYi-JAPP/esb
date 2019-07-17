package com.hengyi.japp.esb.sap;

import com.hengyi.japp.esb.core.Util;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author jzb 2018-04-17
 */
public class Main {
    private static final String ROOT_PATH = "/home/esb/esb-sap";

    public static void main(String[] args) {
        final DeploymentOptions deploymentOptions = new DeploymentOptions();

        final JsonObject config = Util.readJsonObject(ROOT_PATH, "config.json");
        deploymentOptions.setConfig(config);
        final VertxOptions vertxOptions = new VertxOptions()
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS);
        System.out.println("1 + 1 = 2");
        Vertx.vertx(vertxOptions).deployVerticle(MainVerticle.class.getName(), deploymentOptions);
    }
}
