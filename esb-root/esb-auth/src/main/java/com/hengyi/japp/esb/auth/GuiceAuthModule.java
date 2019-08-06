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
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Map;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * 描述：
 *
 * @author jzb 2018-03-21
 */
public class GuiceAuthModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AuthService.class).to(AuthServiceImpl.class);
    }

    @SneakyThrows
    @Provides
    @Singleton
    @Named("esb-auth-config")
    private JsonObject EsbOaConfig(@Named("esb-config") JsonObject esbConfig) {
        final File file = FileUtils.getFile(esbConfig.getString("rootPath"), "esb-auth", "config.json");
        return new JsonObject(MAPPER.readValue(file, Map.class));
    }

    @Provides
    @Singleton
    protected JWTAuth JWTAuth(Vertx vertx, @Named("esb-auth-config") JsonObject esbAuthConfig) {
        final JsonObject jwt = esbAuthConfig.getJsonObject("jwt");
        return JWTAuth.create(vertx, new JWTAuthOptions(jwt));
    }

}
