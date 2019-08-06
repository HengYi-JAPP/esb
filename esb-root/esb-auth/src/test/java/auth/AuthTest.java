package auth;

import com.hengyi.japp.esb.auth.AuthVerticle;
import com.hengyi.japp.esb.core.Constant;
import com.hengyi.japp.esb.core.Util;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

import static org.junit.Assert.assertNotNull;

/**
 * @author jzb 2018-04-17
 */
@RunWith(VertxUnitRunner.class)
public class AuthTest {
    private static final String ROOT_PATH = "/home/esb/esb-auth";
    private static final JsonObject config = Util.readJsonObject(ROOT_PATH, "config.json");
    private static final DeploymentOptions deploymentOptions = new DeploymentOptions()
            .setConfig(config);
    private static Vertx vertx;

    @BeforeClass
    public static void BeforeClass() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        final KeyStore keystore = KeyStore.getInstance("jceks");
        final Path path = Paths.get(ROOT_PATH, "esb-auth.jceks");
        final FileInputStream is = new FileInputStream(path.toFile());
        keystore.load(is, "esb-auth-tomking".toCharArray());
        final Certificate cert = keystore.getCertificate("RS512");
        final PublicKey publicKey = cert.getPublicKey();
        assertNotNull(publicKey);
        final String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        assertNotNull(publicKeyString);
    }

    @Before
    public void Before(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(AuthVerticle.class.getName(), deploymentOptions, context.asyncAssertSuccess());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test1(TestContext context) {
        checkAuth(context, "sap-test", "sap-test-password");
        checkAuth(context, "admin", "tomking");
        checkAuthError(context, "admin", "sddfsf");
    }

    private void checkAuth(TestContext context, final String id, final String password) {
        final Async async = context.async();
        final WebClient webClient = WebClient.create(vertx);
        final JsonObject body = new JsonObject()
                .put("id", id)
                .put("password", password);
        webClient.post(9998, "127.0.0.1", "/api/auth")
                .rxSendJsonObject(body)
                .map(HttpResponse::bodyAsString)
                .doFinally(async::complete)
                .subscribe(
                        it -> {
                            context.assertNotNull(it);
                            final String[] segments = it.split("\\.");
                            final String headerSeg = segments[0];
                            final JsonObject header = new JsonObject(Buffer.buffer(Base64.getUrlDecoder().decode(headerSeg)));
                            context.assertEquals("JWT", header.getString("typ"));
                            context.assertEquals(Constant.JWT.ALG, header.getString("alg"));

                            final String payloadSeg = segments[1];
                            final JsonObject claims = new JsonObject(Buffer.buffer(Base64.getUrlDecoder().decode(payloadSeg)));
                            context.assertEquals(Constant.JWT.ISS, claims.getString("iss"));
                            context.assertEquals(id, claims.getString("sub"));
                            context.assertNotNull(claims.getLong("iat"));
                            context.assertNotNull(claims.getInteger("exp"));

                            final JsonObject jwt = new JsonObject().put("jwt", it);
                            Util.createJwtAuth(vertx).rxAuthenticate(jwt)
                                    .subscribe(user -> {
                                        context.assertNotNull(user);
                                        final JsonObject principal = user.principal();
                                        context.assertNotNull(principal);
                                        context.assertEquals(Constant.JWT.ISS, principal.getString("iss"));
                                        context.assertEquals(id, principal.getString("sub"));
                                    });
                        },
                        context::fail
                );
    }

    private void checkAuthError(TestContext context, String id, String password) {
        final Async async = context.async();
        final WebClient webClient = WebClient.create(vertx);
        final JsonObject body = new JsonObject()
                .put("id", id)
                .put("password", password);
        webClient.post(9998, "127.0.0.1", "/api/schedulerPunch")
                .rxSendJsonObject(body)
                .doOnError(ex -> context.assertNotNull(ex))
                .doFinally(async::complete)
                .subscribe();
    }
}
