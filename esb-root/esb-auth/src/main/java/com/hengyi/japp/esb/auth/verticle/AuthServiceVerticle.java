package com.hengyi.japp.esb.auth.verticle;

import com.github.ixtf.japp.core.exception.JError;
import com.hengyi.japp.esb.auth.application.AuthService;
import com.hengyi.japp.esb.core.verticle.BaseRestAPIVerticle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import static com.hengyi.japp.esb.auth.AuthVerticle.AUTH_INJECTOR;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class AuthServiceVerticle extends BaseRestAPIVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                token().rxCompletionHandler()
        );
    }

    private MessageConsumer<JsonObject> token() {
        return vertx.eventBus().consumer("esb:auth:AuthService:token", reply -> Single.fromCallable(() -> {
            final JsonObject body = reply.body();
            final String id = body.getString("id");
            final String password = body.getString("password");
            final AuthService authService = AUTH_INJECTOR.getInstance(AuthService.class);
            return authService.auth(id, password);
        }).subscribe(reply::reply, err -> {
            if (!(err instanceof JError)) {
                log.error("", err);
            }
            reply.fail(400, err.getLocalizedMessage());
        }));
    }

}
