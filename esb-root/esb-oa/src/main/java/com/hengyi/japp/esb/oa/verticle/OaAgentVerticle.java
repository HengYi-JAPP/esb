package com.hengyi.japp.esb.oa.verticle;

import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.hengyi.japp.esb.core.Util;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.impl.headers.VertxHttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
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

        router.routeWithRegex("^/api/rest/.+").produces(JSON_CONTENT_TYPE).handler(rc -> {
            final HttpServerRequest request = rc.request();
            final HttpMethod httpMethod = request.method();
            final Named restServiceConfigNamed = Names.named("restServiceConfig");
            final Key<JsonObject> restServiceConfigKey = Key.get(JsonObject.class, restServiceConfigNamed);
            final JsonObject restServiceConfig = OA_INJECTOR.getInstance(restServiceConfigKey);
            final String absoluteURI = restServiceConfig.getString("baseUrl") + request.uri().substring(4);
            final VertxHttpHeaders headers = new VertxHttpHeaders();
            final JsonObject headersJsonObject = restServiceConfig.getJsonObject("headers", new JsonObject());
            headersJsonObject.forEach(entry -> headers.add(entry.getKey(), entry.getValue()));
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = tracer.buildSpan(request.uri())
                    .withTag(Tags.HTTP_METHOD, request.rawMethod())
                    .withTag(Tags.HTTP_URL, absoluteURI)
                    .withTag(Tags.COMPONENT, this.getClass().getName())
                    .start();
            final Single<Buffer> result$;
            switch (httpMethod) {
                case POST: {
                    result$ = WebClient.create(vertx).postAbs(absoluteURI)
                            .putHeaders(MultiMap.newInstance(headers))
                            .rxSendBuffer(rc.getBody())
                            .map(HttpResponse::body);
                    break;
                }
                case PUT: {
                    result$ = WebClient.create(vertx).putAbs(absoluteURI)
                            .putHeaders(MultiMap.newInstance(headers))
                            .rxSendBuffer(rc.getBody())
                            .map(HttpResponse::body);
                    break;
                }
                case GET: {
                    result$ = WebClient.create(vertx).getAbs(absoluteURI)
                            .putHeaders(MultiMap.newInstance(headers))
                            .rxSend()
                            .map(HttpResponse::body);
                    break;
                }
                case DELETE: {
                    result$ = WebClient.create(vertx).deleteAbs(absoluteURI)
                            .putHeaders(MultiMap.newInstance(headers))
                            .rxSend()
                            .map(HttpResponse::body);
                    break;
                }
                default: {
                    result$ = Single.just(Buffer.buffer());
                    break;
                }
            }
            result$.subscribe(buffer -> {
                if (span != null) {
                    span.setTag(Tags.HTTP_STATUS, 200);
                    span.setTag(Tags.ERROR, false);
                    span.finish();
                }
                rc.response().end(buffer);
            }, err -> {
                log.error("", err);
                apmError(rc, span, err);
                rc.fail(err);
            });
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
