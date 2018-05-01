package com.hengyi.japp.esb.open.verticle;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.core.verticle.BaseRestAPIVerticle;
import com.hengyi.japp.esb.open.application.TalentService;
import com.hengyi.japp.esb.open.application.TexbeedataService;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;
import org.pac4j.core.config.ConfigFactory;

import java.util.Optional;

import static com.hengyi.japp.esb.open.MainVerticle.GUICE;

/**
 * @author jzb 2018-04-19
 */
public class OpenVerticle extends BaseRestAPIVerticle {
    public static JWTAuth jwtAuth;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        jwtAuth = Util.createJwtAuth(vertx);

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        enableLocalSession(router);
        enableCorsSupport(router);
        final ConfigFactory configFactory = GUICE.getInstance(ConfigFactory.class);
        enablePac4j(configFactory, router);

        addProtectedEndpoint(router, "/api/autoLogin/*", "CasClient");
        final TalentService talentService = GUICE.getInstance(TalentService.class);
        router.mountSubRouter("/api/autoLogin", talentService.router(vertx));

        final TexbeedataService texbeedataService = GUICE.getInstance(TexbeedataService.class);
        router.mountSubRouter("/api/texbeedata", texbeedataService.router(vertx));

        final JsonObject httpConfig = Optional.ofNullable(config())
                .map(it -> it.getJsonObject("http"))
                .orElse(new JsonObject());
        final Integer port = httpConfig.getInteger("port", 9999);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(port)
                .toCompletable()
                .subscribe(startFuture::complete, startFuture::fail);
    }

}
