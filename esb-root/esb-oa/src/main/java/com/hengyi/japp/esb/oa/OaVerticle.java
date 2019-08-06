package com.hengyi.japp.esb.oa;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.core.MainVerticle;
import com.hengyi.japp.esb.oa.verticle.BasicDataServiceVerticle;
import com.hengyi.japp.esb.oa.verticle.HrmServiceVerticle;
import com.hengyi.japp.esb.oa.verticle.OaAgentVerticle;
import com.hengyi.japp.esb.oa.verticle.WorkflowServiceVerticle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2019-08-01
 */
@Slf4j
public class OaVerticle extends MainVerticle {
    public static Injector OA_INJECTOR;

    public static void main(String[] args) {
        Single.fromCallable(() -> deploymentOptions("/home/esb/esb-oa")).flatMap(deploymentOptions -> {
            final Vertx vertx = vertx();
            OA_INJECTOR = Guice.createInjector(new GuiceModule(vertx), new OaGuiceModule());
            return vertx.rxDeployVerticle(OaVerticle.class.getName(), deploymentOptions);
        }).subscribe(
                it -> log.info("===Esb Oa[" + it + "] 启动成功==="),
                err -> log.error("===Esb Oa 启动失败===", err)
        );
    }

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                deployOaAgent().ignoreElement(),
                deployWorkflowService().ignoreElement(),
                deployBasicDataService().ignoreElement()
//                deployHrmServiceVerticle().ignoreElement()
        );
    }

    private Single<String> deployOaAgent() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config());
        return vertx.rxDeployVerticle(OaAgentVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployWorkflowService() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(WorkflowServiceVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployBasicDataService() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(BasicDataServiceVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployHrmServiceVerticle() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(HrmServiceVerticle.class.getName(), deploymentOptions);
    }
}
