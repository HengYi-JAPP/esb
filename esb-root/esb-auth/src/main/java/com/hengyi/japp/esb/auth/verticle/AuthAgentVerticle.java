package com.hengyi.japp.esb.auth.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;

import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;

/**
 * @author jzb 2018-03-18
 */
public class AuthAgentVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*"));
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());

        router.post("/api/auth").produces(TEXT_CONTENT_TYPE).handler(rc -> rc.reroute("/api/token"));
        router.post("/api/token").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final JsonObject message = rc.getBodyAsJson();
            vertx.eventBus().<String>request("esb:auth:AuthService:token", message, ar -> {
                if (ar.succeeded()) {
                    rc.response().end(ar.result().body());
                } else {
                    rc.fail(ar.cause());
                }
            });
        });

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(9998, ar -> startFuture.handle(ar.mapEmpty()));
    }

}
