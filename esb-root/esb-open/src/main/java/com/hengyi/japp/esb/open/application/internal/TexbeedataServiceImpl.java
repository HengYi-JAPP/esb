package com.hengyi.japp.esb.open.application.internal;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.open.application.SignService;
import com.hengyi.japp.esb.open.application.TexbeedataService;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 描述： 千锋物流接口
 *
 * @author jzb 2018-04-11
 */
public class TexbeedataServiceImpl implements TexbeedataService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JsonObject config;
    private final SignService signService;
    private final WebClient webClient;

    @Inject
    TexbeedataServiceImpl(@Named("vertxConfig") JsonObject vertxConfig, SignService signService, WebClient webClient) {
        this.config = vertxConfig.getJsonObject("texbeedata");
        this.signService = signService;
        this.webClient = webClient;
    }

    @Override
    public void addOrder(RoutingContext rc) {
        final HttpServerResponse response = rc.response();
        final String body = signService.encodeToString(rc.getBodyAsString());
        webClient.postAbs(config.getString("addOrderUrl"))
                .putHeader("Authorization", "Bearer " + signService.token(body))
                .rxSendBuffer(Buffer.buffer(body))
                .map(HttpResponse::bodyAsString)
                .subscribe(
                        response::end,
                        ex -> {
                            response.setStatusCode(400).end();
                            log.error("", ex);
                        }
                );
    }

    @Override
    public void delOrder(RoutingContext rc) {
        final HttpServerRequest request = rc.request();
        final HttpServerResponse response = rc.response();
        final String id = request.getParam("id");
        final String body = signService.encodeToString(new JsonObject().put("transId", id));
        webClient.postAbs(config.getString("delOrderUrl"))
                .putHeader("Authorization", "Bearer " + signService.token(body))
                .rxSendBuffer(Buffer.buffer(body))
                .map(HttpResponse::bodyAsString)
                .subscribe(
                        response::end,
                        ex -> {
                            response.setStatusCode(400).end();
                            log.error("", ex);
                        }
                );
    }

    @Override
    public void closeOrder(RoutingContext rc) {
        final HttpServerResponse response = rc.response();
        final String body = signService.encodeToString(rc.getBodyAsString());
        webClient.postAbs(config.getString("closeOrderUrl"))
                .putHeader("Authorization", "Bearer " + signService.token(body))
                .rxSendBuffer(Buffer.buffer(body))
                .map(HttpResponse::bodyAsString)
                .subscribe(
                        response::end,
                        ex -> {
                            response.setStatusCode(400).end();
                            log.error("", ex);
                        }
                );
    }

}
