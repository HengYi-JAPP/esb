package com.hengyi.japp.esb.oa;

import com.hengyi.japp.esb.core.Util;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author jzb 2018-04-17
 */
@Slf4j
public class Main {
    private static final String ROOT_PATH = "/home/esb/esb-oa";

    public static void main(String[] args) {
        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        final JsonObject config = Util.readJsonObject(ROOT_PATH, "config.json");
        deploymentOptions.setConfig(config);
        final VertxOptions vertxOptions = new VertxOptions()
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS);
        Vertx.vertx(vertxOptions).rxDeployVerticle(MainVerticle.class.getName(), deploymentOptions)
                .ignoreElement()
                .subscribe(
                        () -> log.info("===Esb Oa 启动成功==="),
                        err -> log.error("===Esb Oa 启动失败===", err)
                );
    }
}
