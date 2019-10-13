package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.oa.application.OaRestService;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static com.hengyi.japp.esb.core.Constant.JSON_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Constant.TEXT_CONTENT_TYPE;
import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.oa.OaGuiceModule.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class OaAgentVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        final JWTAuth jwtAuth = Util.createJwtAuth(vertx);
        router.route().handler(JWTAuthHandler.create(jwtAuth));

        router.post("/api/yunbiao/WorkflowService/doCreateWorkflowRequest").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "yunbiao:WorkflowService:doCreateWorkflowRequest";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });
        router.post("/api/WorkflowService/doCreateWorkflowRequest").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "WorkflowService:doCreateWorkflowRequest";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });
        router.post("/api/WorkflowService/getWorkflowRequest").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "WorkflowService:getWorkflowRequest";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });
        router.post("/api/WorkflowService/deleteRequest").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "WorkflowService:deleteRequest";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });

        router.post("/api/BasicDataService/getHrmresourceData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "BasicDataService:getHrmresourceData";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });
        router.post("/api/BasicDataService/getDepartmentData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "BasicDataService:getDepartmentData";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });
        router.post("/api/BasicDataService/getSubcompanyData").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "BasicDataService:getSubcompanyData";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });

        router.post("/api/HrmService/getHrmSubcompanyInfo").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "HrmService:getHrmSubcompanyInfo";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });
        router.post("/api/HrmService/getHrmUserInfo").produces(TEXT_CONTENT_TYPE).handler(rc -> {
            final String apmOperationName = "HrmService:getHrmUserInfo";
            request(rc, rc.getBodyAsString(), apmOperationName);
        });

        router.routeWithRegex("^/api/rest/.+").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final OaRestService oaRestService = OA_INJECTOR.getInstance(OaRestService.class);
            oaRestService.handler(rc);
        });

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .listen(9996, ar -> startFuture.handle(ar.mapEmpty()));
    }

    private void request(RoutingContext rc, String message, String apmOperationName) {
        final String address = "esb:oa:" + apmOperationName;
        request(rc, address, message, apmOperationName);
    }

    private void request(RoutingContext rc, String address, String message, String apmOperationName) {
        final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
        final DeliveryOptions deliveryOptions = new DeliveryOptions().setSendTimeout(Duration.ofHours(1).toMillis());
        final Span span = initApm(rc, tracer, this, apmOperationName, address, deliveryOptions, message);
        vertx.eventBus().<String>request(address, message, deliveryOptions, ar -> {
            if (ar.succeeded()) {
                apmSuccess(rc, span, ar.result());
                rc.response().end(ar.result().body());
            } else {
                apmError(rc, span, ar.cause());
                rc.fail(ar.cause());
            }
        });
    }

}
