package com.hengyi.japp.esb.core;

import com.fasterxml.jackson.databind.JsonNode;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.github.ixtf.japp.core.Constant.MAPPER;

/**
 * 描述：
 *
 * @author jzb 2018-03-19
 */
public class Util {
    /**
     * 只做身份验证，无法生成签名
     *
     * @param vertx
     * @return
     */
    public static JWTAuth createJwtAuth(Vertx vertx) {
        final PubSecKeyOptions rs512 = new PubSecKeyOptions()
                .setAlgorithm("RS512")
                .setPublicKey(Constant.JWT.PUBLIC_KEY);
        final JWTAuthOptions jwtAuthOptions = new JWTAuthOptions().addPubSecKey(rs512);
        return JWTAuth.create(vertx, jwtAuthOptions);
    }

    public static Properties readProperties(String first, String... more) {
        try {
            final Path path = Paths.get(first, more);
            final Properties properties = new Properties();
            properties.load(new FileReader(path.toFile()));
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readJson(Class<T> clazz, String json) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject readJsonObject(String first, String... more) {
        try {
            final Path path = Paths.get(first, more);
            final Map map = MAPPER.readValue(path.toFile(), Map.class);
            return new JsonObject(map);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends JsonNode> T readJsonNode(String first, String... more) {
        try {
            final Path path = Paths.get(first, more);
            return (T) MAPPER.readTree(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sqlIn(Collection<String> ss) {
        return ss.stream()
                .parallel()
                .distinct()
                .map(it -> "'" + it + "'")
                .collect(Collectors.joining(","));
    }
}
