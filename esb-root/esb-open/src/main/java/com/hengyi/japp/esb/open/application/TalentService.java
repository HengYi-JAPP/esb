package com.hengyi.japp.esb.open.application;

import com.google.inject.ImplementedBy;
import com.hengyi.japp.esb.open.application.internal.TalentServiceImpl;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * @author jzb 2018-03-18
 */
@ImplementedBy(TalentServiceImpl.class)
public interface TalentService {
    default Router router(Vertx vertx) {
        final Router router = Router.router(vertx);
        router.post("/talent").blockingHandler(this::autoLogin);
        return router;
    }

    void autoLogin(RoutingContext rc);
}
