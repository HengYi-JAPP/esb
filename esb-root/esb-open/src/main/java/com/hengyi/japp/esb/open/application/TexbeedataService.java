package com.hengyi.japp.esb.open.application;

import com.google.inject.ImplementedBy;
import com.hengyi.japp.esb.open.application.internal.TexbeedataServiceImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * 描述：
 *
 * @author jzb 2018-04-11
 */
@ImplementedBy(TexbeedataServiceImpl.class)
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
