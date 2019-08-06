package com.hengyi.japp.esb.auth.verticle;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

/**
 * @author jzb 2019-08-02
 */
public class AuthWorkerVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                vertx.eventBus().<JsonObject>consumer("esb:auth:AuthService:auth", reply -> {
                    final JsonObject jsonObject = reply.body();

                }).rxCompletionHandler()
        );
    }

}
