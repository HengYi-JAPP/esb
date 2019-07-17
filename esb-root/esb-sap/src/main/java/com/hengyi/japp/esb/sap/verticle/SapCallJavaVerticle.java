package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.sap.interfaces.nnfp.NnfpService;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServerRequest;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import static com.hengyi.japp.esb.sap.MainVerticle.GUICE;

/**
 * 描述：
 *
 * @author jzb 2018-03-18
 */
@Slf4j
public class SapCallJavaVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());

        /**
         * todo 重新分配端口，和url地址， sap调用java的端口需要特殊化，不能映射成外网，考虑登入校验
         */
        router.mountSubRouter("/sapCallJava", apiRouter());
        router.mountSubRouter("/api", apiRouter());


        final JsonObject httpConfig = Optional.ofNullable(config())
                .map(it -> it.getJsonObject("sapCallJava"))
                .map(it -> it.getJsonObject("http"))
                .orElse(new JsonObject());
        final Integer port = httpConfig.getInteger("port", 9876);
        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(port)
                .ignoreElement();
    }

    private Router apiRouter() {
        final Router apiRouter = Router.router(vertx);
        apiRouter.post("/rfcs/:rfcName").blockingHandler(this::callJava);
        return apiRouter;
    }

    private void callJava(RoutingContext rc) {
        final HttpServerRequest request = rc.request();
        final HttpServerResponse response = rc.response();
        final String rfcName = request.getParam("rfcName");
        final Single<String> single;
        switch (rfcName) {
            case "ZRFC_FI_SENDGOLENINVOICE": {
                final NnfpService nnfpService = GUICE.getInstance(NnfpService.class);
                single = nnfpService.kpOrderSync(rc.getBodyAsString());
                break;
            }

            case "ZRFC_FI_QUERYGOLENINVOICE": {
                final NnfpService nnfpService = GUICE.getInstance(NnfpService.class);
                single = nnfpService.queryElectricKp(rc.getBodyAsString());
                break;
            }

            default: {
                single = Single.just("ok");
                break;
            }
        }
        single.subscribe(
                response::end,
                ex -> {
                    response.setStatusCode(400).end(ex.getMessage());
                    log.error("", ex);
                }
        );
    }

}
