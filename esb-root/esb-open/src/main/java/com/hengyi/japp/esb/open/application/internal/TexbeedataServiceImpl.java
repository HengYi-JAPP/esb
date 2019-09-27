package com.hengyi.japp.esb.open.application.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.open.application.SignService;
import com.hengyi.japp.esb.open.application.TexbeedataService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 描述： 千锋物流接口
 *
 * @author jzb 2018-04-11
 */
@Slf4j
@Singleton
public class TexbeedataServiceImpl implements TexbeedataService {
    private final WebClient webClient;
    private final JsonObject config;
    private final SignService signService;

    @Inject
    private TexbeedataServiceImpl(Vertx vertx, @Named("vertxConfig") JsonObject vertxConfig, SignService signService) {
        this.webClient = WebClient.create(vertx);
        this.config = vertxConfig.getJsonObject("texbeedata");
        this.signService = signService;
    }

    @Override
    public void addOrder(RoutingContext rc) {
        Future.<HttpResponse<Buffer>>future(promise -> {
            final String body = signService.encodeToString(rc.getBodyAsString());
            webClient.postAbs(config.getString("addOrderUrl"))
                    .putHeader("Authorization", "Bearer " + signService.token(body))
                    .sendBuffer(Buffer.buffer(body), promise);
        }).map(HttpResponse::bodyAsString).setHandler(ar -> {
            if (ar.succeeded()) {
                rc.response().end(ar.result());
            } else {
                log.error("", ar.cause());
                rc.response().setStatusCode(400).end();
            }
        });
    }

    @Override
    public void delOrder(RoutingContext rc) {
        Future.<HttpResponse<Buffer>>future(promise -> {
            final HttpServerRequest request = rc.request();
            final String id = request.getParam("id");
            final String body = signService.encodeToString(new JsonObject().put("transId", id));
            webClient.postAbs(config.getString("delOrderUrl"))
                    .putHeader("Authorization", "Bearer " + signService.token(body))
                    .sendBuffer(Buffer.buffer(body), promise);
        }).map(HttpResponse::bodyAsString).setHandler(ar -> {
            if (ar.succeeded()) {
                rc.response().end(ar.result());
            } else {
                log.error("", ar.cause());
                rc.response().setStatusCode(400).end();
            }
        });
    }

    @Override
    public void closeOrder(RoutingContext rc) {
        Future.<HttpResponse<Buffer>>future(promise -> {
            final String body = signService.encodeToString(rc.getBodyAsString());
            webClient.postAbs(config.getString("closeOrderUrl"))
                    .putHeader("Authorization", "Bearer " + signService.token(body))
                    .sendBuffer(Buffer.buffer(body), promise);
        }).map(HttpResponse::bodyAsString).setHandler(ar -> {
            if (ar.succeeded()) {
                rc.response().end(ar.result());
            } else {
                log.error("", ar.cause());
                rc.response().setStatusCode(400).end();
            }
        });
    }

}
