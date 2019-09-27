package com.hengyi.japp.esb.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
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

    @Provides
    @Singleton
    protected JWTAuth JWTAuth(Vertx vertx, @Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject jwt = vertxConfig.getJsonObject("jwt");
        return JWTAuth.create(vertx, new JWTAuthOptions(jwt));
    }
}
