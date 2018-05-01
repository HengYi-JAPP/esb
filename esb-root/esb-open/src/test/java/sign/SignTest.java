package sign;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 描述：
 * keytool -genkey -keystore esb-open.jceks -storetype jceks -storepass esb-open-tomking -keyalg RSA -keysize 2048 -alias esb-open -keypass esb-open-tomking -sigalg SHA512withRSA -dname "CN=esb-open, OU=hy, O=hengyi, L=hz, ST=zj, C=cn"
 *
 * @author jzb 2018-03-20
 */
public class SignTest {
    private static final String signString = "test";

    // /**
    //  * RS512
    //  *
    //  * @throws NoSuchAlgorithmException
    //  * @throws InvalidKeySpecException
    //  * @throws SignatureException
    //  * @throws InvalidKeyException
    //  */
    // @Test
    // public void test1() throws NoSuchAlgorithmException, InvalidKeySpecException, SignatureException, InvalidKeyException {
    //     final byte[] sign = SignUtil.sign(signString);
    //     final String signOut = Base64.getUrlEncoder().withoutPadding().encodeToString(sign);
    //
    //     final byte[] expected = Base64.getUrlDecoder().decode(signOut);
    //     final byte[] payload = signString.getBytes(UTF_8);
    //     assertTrue(verify(expected, payload));
    // }
    //
    // boolean verify(byte[] expected, byte[] payload) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
    //     final Signature signature = Signature.getInstance("SHA512withRSA");
    //     final KeyFactory kf = KeyFactory.getInstance("RSA");
    //     final X509EncodedKeySpec keyspec = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(PUBLIC_KEY_STRING));
    //     final PublicKey publicKey = kf.generatePublic(keyspec);
    //     signature.initVerify(publicKey);
    //     signature.update(payload);
    //     return signature.verify(expected);
    // }
    //
    // @Test
    // public void test3() throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    //     final JsonObject data = new JsonObject()
    //             .put("id", "testid")
    //             .put("name", "testname");
    //     // post 内容
    //     final String body = Base64.getUrlEncoder().withoutPadding().encodeToString(data.encode().getBytes(UTF_8));
    //
    //     final JsonObject header = new JsonObject()
    //             .put("typ", "JWT")
    //             .put("alg", "RS512");
    //     final String headerSeg = Base64.getUrlEncoder().withoutPadding().encodeToString(header.encode().getBytes(UTF_8));
    //
    //     RandomStringGenerator generator = new RandomStringGenerator.Builder()
    //             .withinRange('a', 'z').build();
    //     final JsonObject payload = new JsonObject()
    //             .put("iss", "hengyi-esb-open")
    //             // 5 分钟后过期
    //             .put("exp", System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5))
    //             .put("timestamp", System.currentTimeMillis())
    //             .put("nonce", generator.generate(6))
    //             .put("content-sha1", DigestUtils.sha1Hex(body));
    //     final String payloadSeg = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.encode().getBytes(UTF_8));
    //
    //     final byte[] sign = SignUtil.sign(headerSeg + "." + payloadSeg);
    //     final String signSeg = Base64.getUrlEncoder().withoutPadding().encodeToString(sign);
    //     System.out.println(headerSeg + "." + payloadSeg + "." + signSeg);
    //     System.out.println(body);
    //
    //
    //     final byte[] expected = Base64.getUrlDecoder().decode(signSeg);
    //     final byte[] bytes = (headerSeg + "." + payloadSeg).getBytes(UTF_8);
    //     System.out.println(verify(expected, bytes));
    // }

    /**
     * header: {"typ":"JWT", alg":"HS256"}
     * eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9
     * <p>
     * claims: {"iss":"joe", exp":1300819380, "http://example.com/is_root":true}
     * eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ
     * <p>
     * sign： {"kty":"oct", "k":"AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow"}
     * dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
     * <p>
     * eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
     */
    @Test
    public void test2() throws NoSuchAlgorithmException, InvalidKeyException {
        final String token = "eyJ0eXAiOiJKV1QiLA0KICJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJqb2UiLA0KICJleHAiOjEzMDA4MTkzODAsDQogImh0dHA6Ly9leGFtcGxlLmNvbS9pc19yb290Ijp0cnVlfQ.dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
        String[] segments = token.split("\\.");

        String headerSeg = segments[0];
        final JsonObject header = new JsonObject(Buffer.buffer(Base64.getUrlDecoder().decode(headerSeg)));
        assertEquals(header.getString("typ"), "JWT");
        assertEquals(header.getString("alg"), "HS256");

        String payloadSeg = segments[1];
        final JsonObject claims = new JsonObject(Buffer.buffer(Base64.getUrlDecoder().decode(payloadSeg)));
        assertEquals(claims.getString("iss"), "joe");
        assertTrue(claims.getLong("exp") == 1300819380);
        assertTrue(claims.getBoolean("http://example.com/is_root"));

        Mac mac = Mac.getInstance("HmacSHA256");
        final String k = "AyM1SysPpbyDfgZld3umj1qzKObwVMkoqQ-EstJQLr_T-1qS0gZH75aKtMN3Yj0iPS4hcgUuTwjAzZr1Z9CAow";
        final byte[] oct = Base64.getUrlDecoder().decode(k);
        final SecretKeySpec key = new SecretKeySpec(oct, "HMacSHA256");
        mac.init(key);
        final byte[] bytes = mac.doFinal((headerSeg + "." + payloadSeg).getBytes(UTF_8));
        assertEquals(Base64.getUrlEncoder().withoutPadding().encodeToString(bytes), segments[2]);
    }


}
