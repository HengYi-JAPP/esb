package com.hengyi.japp.esb.auth.application.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.ixtf.japp.core.exception.JAuthenticationError;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.hengyi.japp.esb.auth.application.AuthService;
import com.hengyi.japp.esb.core.Constant;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * 描述：
 *
 * @author jzb 2018-04-30
 */
@Singleton
public class AuthServiceImpl implements AuthService {
    private final String rootPath;
    private final JWTAuth jwtAuth;

    @Inject
    private AuthServiceImpl(JWTAuth jwtAuth, @Named("rootPath") String rootPath) {
        this.jwtAuth = jwtAuth;
        this.rootPath = rootPath;
    }

    @Override
    public String auth(String id, String password) throws Exception {
        final File file = FileUtils.getFile(rootPath, "users.json");
        final JsonNode users = MAPPER.readTree(file);
        final boolean present = StreamSupport.stream(users.spliterator(), true)
                .filter(it -> Objects.equals(id, it.get("id").asText()))
                .findFirst()
                .filter(it -> Objects.equals(password, it.get("password").asText()))
                .isPresent();
        if (!present) {
            throw new JAuthenticationError();
        }

        // todo 把client的信息放入数据库
        final JWTOptions jwtOptions = new JWTOptions()
                .setIssuer(Constant.JWT.ISS)
                .setAlgorithm(Constant.JWT.ALG)
                .setExpiresInSeconds(Constant.JWT.EXPIRES_IN_SECONDS);
        return jwtAuth.generateToken(new JsonObject().put("sub", id), jwtOptions);
    }
}
