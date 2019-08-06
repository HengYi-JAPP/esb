package com.hengyi.japp.esb.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.auth.application.AuthService;
import com.hengyi.japp.esb.auth.application.internal.AuthServiceImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class AuthGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AuthService.class).to(AuthServiceImpl.class);
    }

    @Provides
    @Singleton
    protected JWTAuth JWTAuth(Vertx vertx, @Named("vertxConfig") JsonObject vertxConfig) {
        final JsonObject jwt = vertxConfig.getJsonObject("jwt");
        return JWTAuth.create(vertx, new JWTAuthOptions(jwt));
    }
}
