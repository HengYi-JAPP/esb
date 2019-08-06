package com.hengyi.japp.esb.agent;

import com.hengyi.japp.esb.core.GuiceModule;
import io.vertx.reactivex.core.Vertx;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceAgentModule extends GuiceModule {

    GuiceAgentModule(Vertx vertx) {
        super(vertx);
    }

}
