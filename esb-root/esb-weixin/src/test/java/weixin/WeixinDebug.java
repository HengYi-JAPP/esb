package weixin;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.weixin.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.reactivex.core.Vertx;

/**
 * 描述：
 *
 * @author jzb 2018-04-30
 */
public class WeixinDebug {
    private static final String ROOT_PATH = "/home/esb/esb-weixin";
    private static final JsonObject config = Util.readJsonObject(ROOT_PATH, "config.json");
    private static final DeploymentOptions deploymentOptions = new DeploymentOptions()
            .setConfig(config);
    private static final DropwizardMetricsOptions dropwizardMetricsOptions = new DropwizardMetricsOptions()
            .setEnabled(true)
            .setJmxEnabled(true);
    private static final VertxOptions vertxOptions = new VertxOptions()
            .setMetricsOptions(dropwizardMetricsOptions);
    private static Vertx vertx;

    public static void main(String[] args) {
        vertx = Vertx.vertx(vertxOptions);
        vertx.deployVerticle(MainVerticle.class.getName(), deploymentOptions);
    }
}
