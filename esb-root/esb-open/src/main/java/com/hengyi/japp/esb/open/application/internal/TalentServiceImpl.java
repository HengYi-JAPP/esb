package com.hengyi.japp.esb.open.application.internal;

import com.github.ixtf.japp.core.J;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.open.application.TalentService;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.MultiMap;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.auth.User;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.Map;
import java.util.Optional;

import static org.pac4j.core.context.HttpConstants.LOCATION_HEADER;

/**
 * 描述：
 *
 * @author jzb 2018-03-26
 */
public class TalentServiceImpl implements TalentService {
    private final JsonObject restApiConfig;
    private final RestApiAccessToken restApiAccessToken = new RestApiAccessToken();
    private final JsonObject autoLoginConfig;
    private final WebClient webClient;

    @Inject
    private TalentServiceImpl(@Named("vertxConfig") JsonObject vertxConfig, WebClient webClient) {
        this.restApiConfig = vertxConfig.getJsonObject("talent").getJsonObject("restApi");
        this.autoLoginConfig = vertxConfig.getJsonObject("talent").getJsonObject("autoLogin");
        this.webClient = webClient;
    }

    @Override
    public void autoLogin(RoutingContext rc) {
        final HttpServerResponse response = rc.response();
        Optional.ofNullable(rc.user())
                .map(User::principal)
                .map(it -> it.getJsonObject("CasClient"))
                .map(it -> it.getString("uid"))
                .map(this::autoLoginAccessTokenByUid)
                .orElse(Single.error(new RuntimeException()))
                .subscribe(
                        access_token -> {
                            final Map<String, String> map = ImmutableMap.of("access_token", access_token);
                            final String url = J.strTpl(autoLoginConfig.getString("autoLoginUrlTpl"), map);
                            System.out.println(url);
                            response.putHeader(LOCATION_HEADER, url).setStatusCode(303).end();
                        },
                        ex -> response.setStatusCode(400).end()
                );
    }

    private Single<String> autoLoginAccessTokenByUid(String uid) {
        return restApiAccessToken.access_token()
                .map(it -> "Bearer " + it)
                .flatMap(authorization -> {
                    final Map<String, String> map = ImmutableMap.of("staffCode", uid);
                    final String url = J.strTpl(restApiConfig.getString("staffCodeUrlTpl"), map);
                    return webClient.getAbs(url).putHeader("authorization", authorization).rxSend();
                })
                .map(HttpResponse::bodyAsJsonObject)
                .flatMap(it -> {
                    final Integer total = it.getInteger("total");
                    if (total < 1) {
                        return Single.error(new RuntimeException());
                    }
                    final long user_id = it.getJsonArray("items")
                            .getJsonObject(0)
                            .getJsonObject("staffDto")
                            .getLong("userId");
                    return autoLoginAccessToken(user_id);
                });
    }

    private Single<String> autoLoginAccessToken(long user_id) {
        final MultiMap form = MultiMap.caseInsensitiveMultiMap()
                .set("tenant_id", autoLoginConfig.getString("tenant_id"))
                .set("user_id", "" + user_id)
                .set("grant_type", autoLoginConfig.getString("grant_type"))
                .set("secret", autoLoginConfig.getString("secret"))
                .set("app_id", autoLoginConfig.getString("app_id"));
        return webClient.postAbs(autoLoginConfig.getString("accesTokenUrl"))
                .rxSendForm(form)
                .map(HttpResponse::bodyAsJsonObject)
                .map(it -> it.getString("access_token"));
    }

    private class RestApiAccessToken {
        private String access_token;
        private long expires_in;

        private Single<String> access_token() {
            if (expires_in > System.currentTimeMillis()) {
                return Single.just(access_token);
            }
            final MultiMap form = MultiMap.caseInsensitiveMultiMap()
                    .set("secret", restApiConfig.getString("secret"))
                    .set("tenant_id", restApiConfig.getString("tenant_id"))
                    .set("grant_type", restApiConfig.getString("grant_type"))
                    .set("app_id", restApiConfig.getString("app_id"));
            return webClient.postAbs(restApiConfig.getString("accesTokenUrl"))
                    .rxSendForm(form)
                    .map(HttpResponse::bodyAsJsonObject)
                    .doOnSuccess(it -> {
                        access_token = it.getString("access_token");
                        expires_in = Long.valueOf(it.getString("expires_in"));
                    })
                    .map(it -> access_token);
        }
    }

}
