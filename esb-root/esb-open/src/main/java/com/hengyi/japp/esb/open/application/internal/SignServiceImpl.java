package com.hengyi.japp.esb.open.application.internal;

import com.google.inject.Inject;
import com.hengyi.japp.esb.open.application.SignService;
import io.vertx.core.json.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.RandomStringGenerator;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 描述： 对外签名
 * keytool -genkey -keystore esb-open.jceks -storetype jceks -storepass esb-open-tomking -keyalg RSA -keysize 2048 -alias esb-open -keypass esb-open-tomking -sigalg SHA512withRSA -dname "CN=esb-open, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-04-19
 */
public class SignServiceImpl implements SignService {
    private final PrivateKey privateKey;

    @Inject
    private SignServiceImpl(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String token(final String body) {
        final JsonObject header = new JsonObject()
                .put("typ", "JWT")
                .put("alg", "RS512");
        final String headerSeg = encodeToString(header);

        final RandomStringGenerator generator = new RandomStringGenerator.Builder()
                .withinRange('a', 'z')
                .build();
        final JsonObject payload = new JsonObject()
                .put("iss", "hengyi-esb-open")
                // 5 分钟后过期
                .put("exp", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))
                .put("timestamp", System.currentTimeMillis())
                .put("nonce", generator.generate(6))
                .put("content-sha1", DigestUtils.sha1Hex(body));
        final String payloadSeg = encodeToString(payload);

        return headerSeg + "." + payloadSeg + "." + sign(headerSeg + "." + payloadSeg);
    }

    private String sign(final String s) {
        Validate.notBlank(s);
        try {
            final Signature sig = Signature.getInstance("SHA512withRSA");
            sig.initSign(privateKey);
            sig.update(s.getBytes(UTF_8));
            return encodeToString(sig.sign());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
