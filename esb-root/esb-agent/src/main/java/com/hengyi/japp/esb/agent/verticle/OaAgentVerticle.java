package com.hengyi.japp.esb.agent.verticle;

import com.hengyi.japp.esb.core.Util;
import io.reactivex.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

import java.time.Duration;

import static com.hengyi.japp.esb.core.Constant.JSON_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;

/**
 * @author jzb 2019-08-02
 */
public class OaAgentVerticle extends AbstractVerticle {
    @Override
    public Completable rxStart() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        final JWTAuth jwtAuth = Util.createJwtAuth(vertx);
        router.route().handler(JWTAuthHandler.create(jwtAuth));

        router.post("/api/WorkflowService/doCreateWorkflowRequest").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String body = rc.getBodyAsString();
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:oa:WorkflowService:doCreateWorkflowRequest", body, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });
        router.post("/api/yunbiao/WorkflowService/doCreateWorkflowRequest").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String body = rc.getBodyAsString();
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:oa:yunbiao:WorkflowService:doCreateWorkflowRequest", body, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });
        router.post("/api/WorkflowService/deleteRequest").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String body = rc.getBodyAsString();
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:oa:WorkflowService:deleteRequest", body, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });

        router.post("/api/BasicDataService/getHrmresourceData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String body = rc.getBodyAsString();
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:oa:BasicDataService:getHrmresourceData", body, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });
        router.post("/api/BasicDataService/getDepartmentData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String body = rc.getBodyAsString();
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:oa:BasicDataService:getDepartmentData", body, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });
        router.post("/api/BasicDataService/getSubcompanyData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String body = rc.getBodyAsString();
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:oa:BasicDataService:getSubcompanyData", body, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });

        router.post("/api/HrmService/getHrmSubcompanyInfo").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String body = rc.getBodyAsString();
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:oa:HrmService:getHrmSubcompanyInfo", body, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });
        router.post("/api/HrmService/getHrmUserInfo").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String body = rc.getBodyAsString();
            final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
            vertx.eventBus().<String>rxSend("esb:oa:HrmService:getHrmUserInfo", body, deliveryOptions)
                    .map(Message::body)
                    .subscribe(rc.response()::end, rc::fail);
        });

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        return vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .rxListen(9996)
                .ignoreElement();
    }
}
