package weixin;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.weixin.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 描述：
 *
 * @author jzb 2018-04-29
 */
@RunWith(VertxUnitRunner.class)
public class WeixinTest {
    private static final String ROOT_PATH = "/home/esb/esb-weixin";
    private static final JsonObject config = Util.readJsonObject(ROOT_PATH, "config.json");
    private static final DeploymentOptions deploymentOptions = new DeploymentOptions()
            .setConfig(config);
    private static Vertx vertx;

    @Before
    public void Before(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), deploymentOptions, context.asyncAssertSuccess());
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void test1(TestContext context) {
        final Async async = context.async();
        final WebClient webClient = WebClient.create(vertx);
        webClient.post(9998, "task.hengyi.com", "/api/auth")
                .rxSendJsonObject(new JsonObject()
                        .put("id", "sap-test")
                        .put("password", "sap-test-password")
                )
                .map(HttpResponse::bodyAsString)
                .flatMap(token -> {
                    final JsonObject body = new JsonObject()
                            .put("startDate", "2018-04-01")
                            .put("endDate", "2018-05-01");
                    return webClient.post(9995, "127.0.0.1", "/api/schedulerPunch")
                            .putHeader("Authorization", "Bearer " + token)
                            .rxSendJsonObject(body);
                })
                .map(HttpResponse::bodyAsString)
                .doFinally(async::complete)
                .subscribe(it -> context.assertEquals("ok", it));
    }
}
