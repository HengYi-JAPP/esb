package com.hengyi.japp.esb.oa;

import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-10-14
 */
public class OaLauncher extends Launcher {
    public static void main(String[] args) {
        new OaLauncher().dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        options.setWorkerPoolSize(10_000)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
    }

    @Override
    public void afterStartingVertx(Vertx vertx) {
        OaGuiceModule.init(vertx);
    }
}
