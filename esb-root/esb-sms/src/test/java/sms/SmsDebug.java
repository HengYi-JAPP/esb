package sms;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sms.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

/**
 * 描述：
 *
 * @author jzb 2018-05-01
 */
public class SmsDebug {
    private static final String ROOT_PATH = "/home/esb/esb-sms";
    private static final JsonObject config = Util.readJsonObject(ROOT_PATH, "config.json");
    private static final DeploymentOptions deploymentOptions = new DeploymentOptions()
            .setConfig(config);
    private static Vertx vertx;

    public static void main(String[] args) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), deploymentOptions);
    }
}
