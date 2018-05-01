package com.hengyi.japp.esb.auth.application.internal;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.auth.application.AuthService;
import com.hengyi.japp.esb.core.Constant;
import com.hengyi.japp.esb.core.Util;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * 描述：
 *
 * @author jzb 2018-04-30
 */
public class AuthServiceImpl implements AuthService {
    private final String rootPath;
    private final JWTAuth jwtAuth;

    @Inject
    private AuthServiceImpl(JWTAuth jwtAuth, @Named("rootPath") String rootPath) {
        this.jwtAuth = jwtAuth;
        this.rootPath = rootPath;
    }

    @Override
    public void auth(RoutingContext rc) {
        final HttpServerResponse response = rc.response();
        final JsonObject body = rc.getBodyAsJson();
        final String id = body.getString("id");
        final String password = body.getString("password");

        final ArrayNode users = Util.readJsonNode(rootPath, "users.json");
        final boolean b = StreamSupport.stream(users.spliterator(), true)
                .filter(it -> Objects.equals(id, it.get("id").asText())
                        && Objects.equals(password, it.get("password").asText())
                )
                .findFirst()
                .isPresent();
        if (!b) {
            response.setStatusCode(401).end();
            return;
        }

        // todo 把client的信息放入数据库
        final JWTOptions jwtOptions = new JWTOptions()
                .setIssuer(Constant.JWT.ISS)
                .setAlgorithm(Constant.JWT.ALG)
                .setExpiresInSeconds(Constant.JWT.EXPIRES_IN_SECONDS);
        final String token = jwtAuth.generateToken(new JsonObject().put("sub", id), jwtOptions);
        response.end(token);
    }
}
