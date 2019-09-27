package com.hengyi.japp.esb.auth.verticle;

import com.github.ixtf.japp.core.exception.JError;
import com.hengyi.japp.esb.auth.application.AuthService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.hengyi.japp.esb.auth.AuthVerticle.AUTH_INJECTOR;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class AuthServiceVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        token().completionHandler(startFuture);
    }

    private MessageConsumer<JsonObject> token() {
        final AuthService authService = AUTH_INJECTOR.getInstance(AuthService.class);
        return vertx.eventBus().consumer("esb:auth:AuthService:token", reply -> Mono.fromCallable(() -> {
            final JsonObject body = reply.body();
            final String id = body.getString("id");
            final String password = body.getString("password");
            return authService.auth(id, password);
        }).subscribe(reply::reply, err -> {
            if (!(err instanceof JError)) {
                log.error("", err);
            }
            reply.fail(400, err.getLocalizedMessage());
        }));
    }

}
