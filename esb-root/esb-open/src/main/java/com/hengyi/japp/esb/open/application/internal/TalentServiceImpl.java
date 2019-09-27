package com.hengyi.japp.esb.open.application.internal;

import com.github.ixtf.japp.core.J;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.open.application.TalentService;
import io.vertx.core.Future;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

/**
 * 描述：
 *
 * @author jzb 2018-03-26
 */
@Slf4j
@Singleton
public class TalentServiceImpl implements TalentService {
    private final WebClient webClient;
    private final JsonObject restApiConfig;
    private final RestApiAccessToken restApiAccessToken = new RestApiAccessToken();
    private final JsonObject autoLoginConfig;

    @Inject
    private TalentServiceImpl(Vertx vertx, @Named("vertxConfig") JsonObject vertxConfig) {
        this.webClient = WebClient.create(vertx);
        this.restApiConfig = vertxConfig.getJsonObject("talent").getJsonObject("restApi");
        this.autoLoginConfig = vertxConfig.getJsonObject("talent").getJsonObject("autoLogin");
    }

    @Override
    public Future<String> autoLoginUrl(User user) {
        return Optional.ofNullable(user)
                .map(User::principal)
                .map(it -> it.getJsonObject("CasClient"))
                .map(it -> it.getString("uid"))
                .map(this::autoLoginAccessTokenByUid)
                .orElse(Future.failedFuture(new RuntimeException()))
                .map(access_token -> {
                    final Map<String, String> map = Map.of("access_token", access_token);
                    return J.strTpl(autoLoginConfig.getString("autoLoginUrlTpl"), map);
                });
    }

    private Future<String> autoLoginAccessTokenByUid(String uid) {
        return restApiAccessToken.access_token()
                .map(it -> "Bearer " + it)
                .compose(authorization -> {
                    final Map<String, String> map = ImmutableMap.of("staffCode", uid);
                    final String url = J.strTpl(restApiConfig.getString("staffCodeUrlTpl"), map);
                    return Future.<HttpResponse<Buffer>>future(promise -> webClient.getAbs(url).putHeader("authorization", authorization).send(promise));
                })
                .map(HttpResponse::bodyAsJsonObject)
                .compose(it -> {
                    final Integer total = it.getInteger("total");
                    if (total < 1) {
                        return Future.failedFuture(new RuntimeException());
                    }
                    final long user_id = it.getJsonArray("items")
                            .getJsonObject(0)
                            .getJsonObject("staffDto")
                            .getLong("userId");
                    return autoLoginAccessToken(user_id);
                });
    }

    private Future<String> autoLoginAccessToken(long user_id) {
        final MultiMap form = MultiMap.caseInsensitiveMultiMap()
                .set("tenant_id", autoLoginConfig.getString("tenant_id"))
                .set("user_id", "" + user_id)
                .set("grant_type", autoLoginConfig.getString("grant_type"))
                .set("secret", autoLoginConfig.getString("secret"))
                .set("app_id", autoLoginConfig.getString("app_id"));
        final String url = autoLoginConfig.getString("accesTokenUrl");
        return Future.<HttpResponse<Buffer>>future(promise -> webClient.postAbs(url).sendForm(form, promise))
                .map(HttpResponse::bodyAsJsonObject)
                .map(it -> it.getString("access_token"));
    }

    private class RestApiAccessToken {
        private String access_token;
        private long expires_in;

        private Future<String> access_token() {
            if (expires_in > System.currentTimeMillis()) {
                return Future.succeededFuture(access_token);
            }
            final MultiMap form = MultiMap.caseInsensitiveMultiMap()
                    .set("secret", restApiConfig.getString("secret"))
                    .set("tenant_id", restApiConfig.getString("tenant_id"))
                    .set("grant_type", restApiConfig.getString("grant_type"))
                    .set("app_id", restApiConfig.getString("app_id"));
            final String url = restApiConfig.getString("accesTokenUrl");
            return Future.<HttpResponse<Buffer>>future(promise -> webClient.postAbs(url).sendForm(form, promise))
                    .map(HttpResponse::bodyAsJsonObject)
                    .map(it -> {
                        access_token = it.getString("access_token");
                        expires_in = Long.valueOf(it.getString("expires_in"));
                        return access_token;
                    });
        }
    }

}
