package com.hengyi.japp.esb.core;

import io.vertx.core.VertxOptions;

/**
 * @author jzb 2019-10-13
 */
public class Launcher extends io.vertx.core.Launcher {
    public static void main(String[] args) {
        System.out.println(args);
        new Launcher().dispatch(args);
    }

    @Override
    public void beforeStartingVertx(VertxOptions options) {
        options.getEventBusOptions().setClustered(true);
    }
}
