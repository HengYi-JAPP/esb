package com.hengyi.japp.esb.auth.verticle;

import com.hengyi.japp.esb.auth.application.AuthService;
import com.hengyi.japp.esb.core.verticle.BaseRestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import java.util.Optional;

import static com.hengyi.japp.esb.auth.MainVerticle.GUICE;
import static com.hengyi.japp.esb.auth.MainVerticle.metricsService;

/**
 * @author jzb 2018-03-18
 */
public class AuthVerticle extends BaseRestAPIVerticle {
    public static JWTAuth jwtAuth;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions(config().getJsonObject("jwt")));

        final Router router = Router.router(vertx);
        enableCorsSupport(router);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());

        router.route("/metrics").produces("application/json").blockingHandler(rc -> {
            final JsonObject metrics = metricsService.getMetricsSnapshot(vertx);
            rc.response().end(metrics.encode());
        });

        final AuthService authService = GUICE.getInstance(AuthService.class);
        router.mountSubRouter("/api", authService.router(vertx));

        final JsonObject httpConfig = Optional.ofNullable(config())
                .map(it -> it.getJsonObject("http"))
                .orElse(new JsonObject());
        final Integer port = httpConfig.getInteger("port", 9998);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(port)
                .toCompletable()
                .subscribe(startFuture::complete, startFuture::fail);
    }

}
