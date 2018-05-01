package com.hengyi.japp.esb.open.application;

import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.Validate;

import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 描述：
 *
 * @author jzb 2018-04-19
 */
public interface SignService {

    default String encodeToString(final byte[] bytes) {
        Validate.notNull(bytes);
        final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    default String encodeToString(final String s) {
        Validate.notBlank(s);
        final byte[] bytes = s.getBytes(UTF_8);
        return encodeToString(bytes);
    }

    default String encodeToString(final JsonObject o) {
        Validate.notNull(o);
        return encodeToString(o.encode());
    }

    String token(String body);
}
