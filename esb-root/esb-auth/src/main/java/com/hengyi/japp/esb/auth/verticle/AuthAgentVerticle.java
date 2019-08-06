package com.hengyi.japp.esb.auth.verticle;

import com.hengyi.japp.esb.core.verticle.BaseRestAPIVerticle;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.dropwizard.MetricsService;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import static com.hengyi.japp.esb.core.Constant.JSON_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;

/**
 * @author jzb 2018-03-18
 */
public class AuthAgentVerticle extends BaseRestAPIVerticle {

    @Override
    public Completable rxStart() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());

        router.route("/metrics").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final JsonObject metrics = MetricsService.create(vertx).getMetricsSnapshot(vertx);
            rc.response().end(metrics.encode());
        });

        router.post("/api/auth").produces(TEXT_CONTENT_TYPE).handler(rc -> rc.reroute("/api/token"));
        router.post("/api/token").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final JsonObject message = rc.getBodyAsJson();
            vertx.eventBus().<String>rxSend("esb:auth:AuthService:token", message)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });
        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(9998)
                .ignoreElement();
    }

}
