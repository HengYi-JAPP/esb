package auth;

import com.hengyi.japp.esb.auth.AuthVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-04-17
 */
@Slf4j
public class AuthDebug {
    public static void main(String[] args) {
        final VertxOptions vertxOptions = new VertxOptions()
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.MINUTES)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
        final Vertx vertx = Vertx.vertx(vertxOptions);

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        vertx.deployVerticle(AuthVerticle.class, deploymentOptions, ar -> {
            if (ar.succeeded()) {
                log.info("===Esb Auth[" + ar.result() + "] 启动成功===");
            } else {
                log.error("===Esb Auth 启动失败===", ar.cause());
            }
        });
    }
}
