package com.hengyi.japp.esb.auth.verticle;

import io.reactivex.Completable;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;

/**
 * @author jzb 2019-08-02
 */
public class AuthAgentVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());

        router.post("/api/auth").produces(TEXT_CONTENT_TYPE).blockingHandler(rc -> {
            final JsonObject message = rc.getBodyAsJson();
            vertx.eventBus().<String>rxSend("esb:auth:AuthService:auth", message)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        return vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .rxListen(9998)
                .ignoreElement();
    }

}
