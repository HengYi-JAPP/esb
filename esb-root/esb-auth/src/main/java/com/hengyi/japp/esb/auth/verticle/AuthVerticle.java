package com.hengyi.japp.esb.auth.verticle;

import com.hengyi.japp.esb.auth.application.AuthService;
import com.hengyi.japp.esb.core.verticle.BaseRestAPIVerticle;
import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.dropwizard.MetricsService;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import java.util.Optional;

import static com.hengyi.japp.esb.auth.MainVerticle.GUICE;

/**
 * @author jzb 2018-03-18
 */
public class AuthVerticle extends BaseRestAPIVerticle {

    @Override
    public Completable rxStart() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        enableCorsSupport(router);

        router.route("/metrics").produces("application/json").blockingHandler(rc -> {
            final JsonObject metrics = MetricsService.create(vertx).getMetricsSnapshot(vertx);
            rc.response().end(metrics.encode());
        });

        final AuthService authService = GUICE.getInstance(AuthService.class);
        router.mountSubRouter("/api", authService.router(vertx));

        final JsonObject httpConfig = Optional.ofNullable(config())
                .map(it -> it.getJsonObject("http"))
                .orElse(new JsonObject());
        final Integer port = httpConfig.getInteger("port", 9998);
        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(port)
                .ignoreElement();
    }

}
