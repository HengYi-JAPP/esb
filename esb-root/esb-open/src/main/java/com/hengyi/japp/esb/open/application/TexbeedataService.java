package com.hengyi.japp.esb.open.application;

import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;

/**
 * 描述：
 *
 * @author jzb 2018-04-11
 */
public interface TexbeedataService {
    default Router router(Vertx vertx) {
        final Router router = Router.router(vertx);
        router.post("/addOrder").blockingHandler(this::addOrder);
        router.post("/delOrder/:id").blockingHandler(this::delOrder);
        router.post("/closeOrder").blockingHandler(this::closeOrder);
        return router;
    }

    void addOrder(RoutingContext rc);

    void delOrder(RoutingContext rc);

    void closeOrder(RoutingContext rc);
}
