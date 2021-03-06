package sap;

import com.hengyi.japp.esb.core.Util;
import com.hengyi.japp.esb.sap.MainVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

import java.util.concurrent.TimeUnit;

/**
 * 描述：
 *
 * @author jzb 2018-04-17
 */
public class SapDebug {
    private static final String ROOT_PATH = "/home/esb/esb-sap";

    public static void main(String[] args) {
        final DeploymentOptions deploymentOptions = new DeploymentOptions();

        final JsonObject config = Util.readJsonObject(ROOT_PATH, "config.json");
        deploymentOptions.setConfig(config);
        final VertxOptions vertxOptions = new VertxOptions().setMaxWorkerExecuteTime(2).setMaxWorkerExecuteTimeUnit(TimeUnit.MINUTES);
        Vertx.vertx(vertxOptions).rxDeployVerticle(MainVerticle.class.getName(), deploymentOptions)
                .subscribe();
    }
}
