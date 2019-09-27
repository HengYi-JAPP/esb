package com.hengyi.japp.esb.oa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.oa.verticle.BasicDataServiceVerticle;
import com.hengyi.japp.esb.oa.verticle.HrmServiceVerticle;
import com.hengyi.japp.esb.oa.verticle.OaAgentVerticle;
import com.hengyi.japp.esb.oa.verticle.WorkflowServiceVerticle;
import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-08-01
 */
@Slf4j
public class OaVerticle extends AbstractVerticle {
    public static String OA_MODULE = "esb-oa";
    public static Injector OA_INJECTOR;

    public static void main(String[] args) {
        final VertxOptions vertxOptions = new VertxOptions()
                .setWorkerPoolSize(1000)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
        final Vertx vertx = Vertx.vertx(vertxOptions);
        OA_INJECTOR = Guice.createInjector(new GuiceModule(vertx, OA_MODULE), new OaGuiceModule());

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        vertx.deployVerticle(OaVerticle.class, deploymentOptions, ar -> {
            if (ar.succeeded()) {
                log.info("===Esb Oa[" + ar.result() + "] 启动成功===");
            } else {
                log.error("===Esb Oa 启动失败===", ar.cause());
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final CompositeFuture deployWorker = CompositeFuture.all(deployWorkflowService(), deployBasicDataService(), deployHrmService());
        deployWorker.compose(f -> deployAgent()).<Void>mapEmpty().setHandler(startFuture);
    }

    private Future<String> deployAgent() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config()).setInstances(20);
            vertx.deployVerticle(OaAgentVerticle.class, deploymentOptions, promise);
        });
    }

    private Future<String> deployWorkflowService() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setWorker(true)
                    .setInstances(1000)
                    .setMaxWorkerExecuteTime(1)
                    .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS);
            vertx.deployVerticle(WorkflowServiceVerticle.class, deploymentOptions, promise);
        });
    }

    private Future<String> deployBasicDataService() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setWorker(true)
                    .setInstances(1000)
                    .setMaxWorkerExecuteTime(1)
                    .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS);
            vertx.deployVerticle(BasicDataServiceVerticle.class, deploymentOptions, promise);
        });
    }

    private Future<String> deployHrmService() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions()
                    .setWorker(true)
                    .setInstances(1000)
                    .setMaxWorkerExecuteTime(1)
                    .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS);
            vertx.deployVerticle(HrmServiceVerticle.class, deploymentOptions, promise);
        });
    }

}
