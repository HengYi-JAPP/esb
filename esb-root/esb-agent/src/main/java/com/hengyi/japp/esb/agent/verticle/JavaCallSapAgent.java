package com.hengyi.japp.esb.agent.verticle;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.hengyi.japp.esb.sap.verticle.JavaCallSapWorker;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import io.reactivex.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import java.time.Duration;
import java.util.Optional;

import static com.hengyi.japp.esb.core.Constant.JSON_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;

/**
 * @author jzb 2018-03-18
 */
public class JavaCallSapAgent extends AbstractVerticle {

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
        router.put("/api/log").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            JavaCallSapWorker.isLog = true;
            rc.response().end("JavaCallSapWorker.isLog=" + JavaCallSapWorker.isLog);
        });
        router.delete("/api/log").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            JavaCallSapWorker.isLog = false;
            rc.response().end("JavaCallSapWorker.isLog=" + JavaCallSapWorker.isLog);
        });

        router.post("/api/rfcs/:rfcName").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String rfcName = rc.pathParam("rfcName");
            final String body = rc.getBodyAsString();
            final JsonObject message = new JsonObject().put("rfcName", rfcName).put("body", body);
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:sap:JavaCallSap", message, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });

        final JsonObject httpConfig = Optional.ofNullable(config())
                .map(it -> it.getJsonObject("javaCallSap"))
                .map(it -> it.getJsonObject("http"))
                .orElse(new JsonObject());
        final Integer port = httpConfig.getInteger("port", 9997);
        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(port)
                .ignoreElement();
    }

}
