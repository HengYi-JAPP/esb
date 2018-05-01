package com.hengyi.japp.esb.sap.verticle;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sap.SapUtil;
import com.hengyi.japp.esb.sap.application.internal.JcoDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.ResponseContentTypeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author jzb 2018-03-18
 */
public class JavaCallSapVerticle extends AbstractVerticle {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private JWTAuth jwtAuth;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        jwtAuth = Util.createJwtAuth(vertx);

        final Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseContentTypeHandler.create());
        router.route().handler(JWTAuthHandler.create(jwtAuth));
        router.mountSubRouter("/api", apiRouter());

        final JsonObject httpConfig = Optional.ofNullable(config())
                .map(it -> it.getJsonObject("javaCallSap"))
                .map(it -> it.getJsonObject("http"))
                .orElse(new JsonObject());
        final Integer port = httpConfig.getInteger("port", 9997);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(port)
                .toCompletable()
                .subscribe(startFuture::complete, startFuture::fail);
    }

    private Router apiRouter() {
        final Router apiRouter = Router.router(vertx);
        apiRouter.post("/rfcs/:rfcName").produces("application/json").blockingHandler(this::callSap);
        return apiRouter;
    }

    private void callSap(RoutingContext rc) {
        final String rfcName = rc.request().getParam("rfcName");
        final String body = rc.getBodyAsString();
        try {
            final JCoDestination dest = JCoDestinationManager.getDestination(JcoDataProvider.KEY);
            final JCoFunction f = dest.getRepository().getFunctionTemplate(rfcName).getFunction();
            SapUtil.setParam(f, body);
            f.execute(dest);
            SapUtil.params2String(f);
            rc.response().end(SapUtil.params2String(f));
        } catch (JCoException e) {
            log.error("", e);
            rc.response().setStatusCode(400).end();
        }
    }

}
