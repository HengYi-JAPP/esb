package com.hengyi.japp.esb.sap.verticle;

import io.reactivex.Completable;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;

/**
 * 描述：
 *
 * @author jzb 2018-03-18
 */
public class SapCallJavaAgentVerticle extends AbstractVerticle {

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

        final HttpServerOptions httpServerOptions = new HttpServerOptions()
                .setDecompressionSupported(true)
                .setCompressionSupported(true);
        return vertx.createHttpServer(httpServerOptions)
                .requestHandler(router)
                .rxListen(9876)
                .ignoreElement();
    }

    private Router apiRouter() {
        final Router apiRouter = Router.router(vertx);
        apiRouter.post("/rfcs/:rfcName").handler(rc -> {
            final String rfcName = rc.pathParam("rfcName");
            switch (rfcName) {
                case "ZRFC_FI_SENDGOLENINVOICE": {
                    vertx.eventBus().<String>rxSend("esb:sap:NnfpService:kpOrderSync", rc.getBodyAsString())
                            .map(Message::body)
                            .subscribe(rc.response()::end, rc::fail);
                    return;
                }

                case "ZRFC_FI_QUERYGOLENINVOICE": {
                    vertx.eventBus().<String>rxSend("esb:sap:NnfpService:queryElectricKp", rc.getBodyAsString())
                            .map(Message::body)
                            .subscribe(rc.response()::end, rc::fail);
                    return;
                }
            }
            rc.response().end();
        });
        return apiRouter;
    }

}
