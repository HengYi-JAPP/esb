package com.hengyi.japp.esb.auth;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.core.GuiceModule;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class AuthGuiceModule extends AbstractModule {
    public static Injector AUTH_INJECTOR;

    synchronized public static void init(Vertx vertx) {
        if (AUTH_INJECTOR == null) {
            AUTH_INJECTOR = Guice.createInjector(new GuiceModule(vertx, "esb-auth"), new AuthGuiceModule());
        }
    }

    @Provides
    @Singleton
    protected JWTAuth JWTAuth(Vertx vertx, @Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject jwt = vertxConfig.getJsonObject("jwt");
        return JWTAuth.create(vertx, new JWTAuthOptions(jwt));
    }
}
