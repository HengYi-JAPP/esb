package com.hengyi.japp.esb.weixin.verticle;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.weixin.application.SchedulerPunch;
import com.hengyi.japp.esb.weixin.application.WeixinService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import java.util.Optional;

import static com.hengyi.japp.esb.weixin.MainVerticle.GUICE;

/**
 * 描述：
 *
 * @author jzb 2018-04-11
 */
public class WeixinVerticle extends BaseRestAPIVerticle {
    private JWTAuth jwtAuth;

    @Override
    public void start(Future<Void> startFuture) {
        jwtAuth = Util.createJwtAuth(vertx);

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        router.route().handler(JWTAuthHandler.create(jwtAuth));

        final SchedulerPunch schedulerPunch = GUICE.getInstance(SchedulerPunch.class);
        router.mountSubRouter("/api", schedulerPunch.router(vertx));

        final WeixinService weixinService = GUICE.getInstance(WeixinService.class);
        router.mountSubRouter("/api", weixinService.router(vertx));

        final JsonObject httpConfig = Optional.ofNullable(config())
                .map(it -> it.getJsonObject("http"))
                .orElse(new JsonObject());
        final Integer port = httpConfig.getInteger("port", 9995);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(port)
                .toCompletable()
                .subscribe(startFuture::complete, startFuture::fail);
    }
}
