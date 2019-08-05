package com.hengyi.japp.esb.oa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.oa.verticle.BasicDataServiceVerticle;
import com.hengyi.japp.esb.oa.verticle.HrmServiceVerticle;
import com.hengyi.japp.esb.oa.verticle.OaAgentVerticle;
import com.hengyi.japp.esb.oa.verticle.WorkflowServiceVerticle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.AbstractVerticle;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-08-01
 */
public class MainVerticle extends AbstractVerticle {
    public static Injector GUICE;

    @Override
    public Completable rxStart() {
        GUICE = Guice.createInjector(new GuiceOaModule(vertx));
        return Completable.mergeArray(
                deployOaAgent().ignoreElement(),
                deployWorkflowService().ignoreElement(),
                deployBasicDataService().ignoreElement(),
                deployHrmServiceVerticle().ignoreElement()
        );
    }

    private Single<String> deployOaAgent() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setInstances(20);
        return vertx.rxDeployVerticle(OaAgentVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployWorkflowService() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setWorker(true)
                .setConfig(config())
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(WorkflowServiceVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployBasicDataService() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setWorker(true)
                .setConfig(config())
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(BasicDataServiceVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployHrmServiceVerticle() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setWorker(true)
                .setConfig(config())
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(HrmServiceVerticle.class.getName(), deploymentOptions);
    }
}
