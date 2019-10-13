package test;

import com.hengyi.japp.esb.sap.callback.LoaGuiceModule;
import com.hengyi.japp.esb.sap.callback.LoaVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import loa.biz.LOAApp;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-10-10
 */
@Slf4j
public class LoaDebug {

    public static void main(String[] args) {
        final VertxOptions vertxOptions = new VertxOptions()
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
        final Vertx vertx = Vertx.vertx(vertxOptions);
        LoaGuiceModule.init(vertx);

        LoaGuiceModule.getInstance(LOAApp.class);

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        vertx.deployVerticle(LoaVerticle.class, deploymentOptions, ar -> {
            if (ar.succeeded()) {
                log.info("===Esb Loa[" + ar.result() + "] 启动成功===");
            } else {
                log.error("===Esb Loa 启动失败===", ar.cause());
            }
        });
    }
}
