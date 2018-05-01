package com.hengyi.japp.esb.auth;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hengyi.japp.esb.auth.application.AuthService;
import com.hengyi.japp.esb.auth.application.internal.AuthServiceImpl;
import com.hengyi.japp.esb.core.GuiceModule;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceAuthModule extends GuiceModule {

    GuiceAuthModule(Vertx vertx) {
        super(vertx);
    }

    @Override
    protected void configure() {
        bind(AuthService.class).to(AuthServiceImpl.class);
    }

    @Provides
    @Singleton
    protected JWTAuth JWTAuth() {
        final JsonObject jwt = vertxConfig().getJsonObject("jwt");
        return JWTAuth.create(vertx, new JWTAuthOptions(jwt));
    }
}
