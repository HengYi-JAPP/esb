package oa;

import com.hengyi.japp.esb.oa.OaGuiceModule;
import com.hengyi.japp.esb.oa.OaVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-10-14
 */
@Slf4j
public class OaDebug {
    public static void main(String[] args) {
        final VertxOptions vertxOptions = new VertxOptions()
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
        final Vertx vertx = Vertx.vertx(vertxOptions);
        OaGuiceModule.init(vertx);

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        vertx.deployVerticle(OaVerticle.class, deploymentOptions, ar -> {
            if (ar.succeeded()) {
                log.info("===Esb Oa[" + ar.result() + "] 启动成功===");
            } else {
                log.error("===Esb Oa 启动失败===", ar.cause());
            }
        });
    }
}