package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.reactivex.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static com.hengyi.japp.esb.core.Constant.JSON_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.sap.SapVerticle.SAP_INJECTOR;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class JavaCallSapAgentVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        final JWTAuth jwtAuth = Util.createJwtAuth(vertx);
        router.route().handler(JWTAuthHandler.create(jwtAuth));

        router.get("/api/caches/clear").produces(TEXT_CONTENT_TYPE).handler(rc -> Completable.fromAction(() -> {
            final JCoDestination dest = JCoDestinationManager.getDestination(JcoDataProvider.KEY);
            dest.getRepository().clear();
        }).subscribe(() -> rc.response().end("caches.clear=ok"), rc::fail));

        router.post("/api/rfcs/:rfcName").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:sap:JavaCallSap";
            final String rfcName = rc.pathParam("rfcName");
            final String body = rc.getBodyAsString();
            final JsonObject message = new JsonObject().put("rfcName", rfcName).put("body", body);
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            final Tracer tracer = SAP_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(rc, tracer, this, rfcName, address, deliveryOptions, body);
            vertx.eventBus().<String>rxSend(address, message, deliveryOptions).subscribe(reply -> {
                apmSuccess(rc, span, reply);
                rc.response().end(reply.body());
            }, err -> {
                apmError(rc, span, err);
                rc.fail(err);
            });
        });

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        return vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .rxListen(9997)
                .ignoreElement();
    }

}
