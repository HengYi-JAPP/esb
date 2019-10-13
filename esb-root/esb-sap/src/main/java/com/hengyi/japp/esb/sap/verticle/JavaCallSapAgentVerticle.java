package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sap.SapGuiceModule;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

import static com.hengyi.japp.esb.core.Constant.JSON_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Util.*;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class JavaCallSapAgentVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());

        // 无token api
        router.get("/api/caches/clear").produces(TEXT_CONTENT_TYPE).handler(rc -> Mono.fromCallable(() -> {
            final JCoDestination dest = JCoDestinationManager.getDestination(JcoDataProvider.KEY);
            dest.getRepository().clear();
            return "caches.clear=ok";
        }).subscribeOn(Schedulers.elastic()).subscribe(rc.response()::end, rc::fail));

        final JWTAuth jwtAuth = Util.createJwtAuth(vertx);
        router.route().handler(JWTAuthHandler.create(jwtAuth));
        router.post("/api/rfcs/:rfcName").produces(JSON_CONTENT_TYPE).handler(rc -> request(rc, "esb:sap:JavaCallSap"));
        router.post("/api/async/rfcs/:rfcName").produces(JSON_CONTENT_TYPE).handler(rc -> request(rc, "esb:sap:async:JavaCallSap"));

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .listen(9997, ar -> startFuture.handle(ar.mapEmpty()));
    }

    private void request(RoutingContext rc, String address) {
        final Tracer tracer = SapGuiceModule.getInstance(Tracer.class);
        final String rfcName = rc.pathParam("rfcName");
        final String body = rc.getBodyAsString();
        final JsonObject message = new JsonObject().put("rfcName", rfcName).put("body", body);
        final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
        final Span span = initApm(rc, tracer, this, rfcName, address, deliveryOptions, body);
        vertx.eventBus().<String>request(address, message, deliveryOptions, ar -> {
            if (ar.succeeded()) {
                apmSuccess(rc, span, ar.result());
                final String result = ar.result().body();
                if (result == null) {
                    rc.response().end();
                } else {
                    rc.response().end(result);
                }
            } else {
                apmError(rc, span, ar.cause());
                rc.fail(ar.cause());
            }
        });
    }

}
