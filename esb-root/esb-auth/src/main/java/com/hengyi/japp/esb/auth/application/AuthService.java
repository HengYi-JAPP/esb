package com.hengyi.japp.esb.auth.application;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author jzb 2018-03-18
 */
public interface AuthService {

    default Router router(Vertx vertx) {
        final Router router = Router.router(vertx);
        router.post("/auth").produces("text/plain").blockingHandler(this::auth);
        return router;
    }

    void auth(RoutingContext rc);
}
