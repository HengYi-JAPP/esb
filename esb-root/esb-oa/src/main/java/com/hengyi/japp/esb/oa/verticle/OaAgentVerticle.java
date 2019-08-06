package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.core.Util;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.reactivex.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static com.hengyi.japp.esb.core.Constant.JSON_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class OaAgentVerticle extends AbstractVerticle {
    @Override
    public Completable rxStart() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        final JWTAuth jwtAuth = Util.createJwtAuth(vertx);
        router.route().handler(JWTAuthHandler.create(jwtAuth));

        router.post("/api/WorkflowService/doCreateWorkflowRequest").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:oa:WorkflowService:doCreateWorkflowRequest";
            rxSend(rc, address, rc.getBodyAsString(), "WorkflowService:doCreateWorkflowRequest");
        });
        router.post("/api/yunbiao/WorkflowService/doCreateWorkflowRequest").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:oa:yunbiao:WorkflowService:doCreateWorkflowRequest";
            rxSend(rc, address, rc.getBodyAsString(), "yunbiao:WorkflowService:doCreateWorkflowRequest");
        });
        router.post("/api/WorkflowService/deleteRequest").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:oa:WorkflowService:deleteRequest";
            rxSend(rc, address, rc.getBodyAsString(), "WorkflowService:deleteRequest");
        });

        router.post("/api/BasicDataService/getHrmresourceData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:oa:BasicDataService:getHrmresourceData";
            rxSend(rc, address, rc.getBodyAsString(), "BasicDataService:getHrmresourceData");
        });
        router.post("/api/BasicDataService/getDepartmentData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:oa:BasicDataService:getDepartmentData";
            rxSend(rc, address, rc.getBodyAsString(), "BasicDataService:getDepartmentData");
        });
        router.post("/api/BasicDataService/getSubcompanyData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:oa:BasicDataService:getSubcompanyData";
            rxSend(rc, address, rc.getBodyAsString(), "BasicDataService:getSubcompanyData");
        });

        router.post("/api/HrmService/getHrmSubcompanyInfo").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:oa:HrmService:getHrmSubcompanyInfo";
            rxSend(rc, address, rc.getBodyAsString(), "HrmService:getHrmSubcompanyInfo");
        });
        router.post("/api/HrmService/getHrmUserInfo").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String address = "esb:oa:HrmService:getHrmUserInfo";
            rxSend(rc, address, rc.getBodyAsString(), "HrmService:getHrmUserInfo");
        });

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        return vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .rxListen(9996)
                .ignoreElement();
    }

    private void rxSend(RoutingContext rc, String address, String message, String apmOperationName) {
        final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
        final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
        final Span span = initApm(rc, tracer, this, apmOperationName, address, deliveryOptions, message);
        vertx.eventBus().<String>rxSend(address, message, deliveryOptions).subscribe(reply -> {
            apmSuccess(rc, span, reply);
            rc.response().end(reply.body());
        }, err -> {
            apmError(rc, span, err);
            rc.fail(err);
        });
    }

}
