package com.hengyi.japp.esb.weixin.application;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;

/**
 * 描述：
 *
 * @author jzb 2018-04-30
 */
public interface WeixinService {

    default Router router(Vertx vertx) {
        final Router router = Router.router(vertx);
        router.post("/agents/:agentId").blockingHandler(rc -> {
            final JsonObject body = rc.getBodyAsJson();
            rc.response().end("ok");
        });
        return router;
    }
}
