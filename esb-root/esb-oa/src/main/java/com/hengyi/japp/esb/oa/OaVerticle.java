package com.hengyi.japp.esb.oa;

import com.hengyi.japp.esb.oa.verticle.BasicDataServiceVerticle;
import com.hengyi.japp.esb.oa.verticle.HrmServiceVerticle;
import com.hengyi.japp.esb.oa.verticle.OaAgentVerticle;
import com.hengyi.japp.esb.oa.verticle.WorkflowServiceVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-08-01
 */
@Slf4j
public class OaVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        OaGuiceModule.init(vertx);
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
