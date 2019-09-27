package com.hengyi.japp.esb.sms.verticle;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sms.application.SchedulerSend1818;
import com.hengyi.japp.esb.sms.application.SmsService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import java.util.Optional;

import static com.hengyi.japp.esb.sms.MainVerticle.GUICE;

/**
 * 描述：
 *
 * @author jzb 2018-03-22
 */
public class SmsVerticle extends BaseRestAPIVerticle {
    private JWTAuth jwtAuth;

    @Override
    public void start(Future<Void> startFuture) {
        jwtAuth = Util.createJwtAuth(vertx);

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        router.route().handler(JWTAuthHandler.create(jwtAuth));

        final SchedulerSend1818 schedulerSend18181 = GUICE.getInstance(SchedulerSend1818.class);
        router.mountSubRouter("/api", schedulerSend18181.router(vertx));

        final SmsService smsService = GUICE.getInstance(SmsService.class);
        router.mountSubRouter("/api", smsService.router(vertx));

        final JsonObject httpConfig = Optional.ofNullable(config())
                .map(it -> it.getJsonObject("http"))
                .orElse(new JsonObject());
        final Integer port = httpConfig.getInteger("port", 9996);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(port)
                .toCompletable()
                .subscribe(startFuture::complete, startFuture::fail);
    }

}
