package com.hengyi.japp.esb.open;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.core.MainVerticle;
import com.hengyi.japp.esb.open.verticle.OpenAgentVerticle;
import com.hengyi.japp.esb.open.verticle.TalentServiceVerticle;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.DeploymentOptions;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class OpenVerticle extends MainVerticle {
    public static Injector OPEN_INJECTOR;

    public static void main(String[] args) {
        Single.fromCallable(() -> deploymentOptions("/home/esb/esb-open")).flatMap(deploymentOptions -> {
            final Vertx vertx = vertx();
            OPEN_INJECTOR = Guice.createInjector(new GuiceModule(vertx), new OpenGuiceModule());
            return vertx.rxDeployVerticle(OpenVerticle.class.getName(), deploymentOptions);
        }).subscribe(
                it -> log.info("===Esb Open[" + it + "] 启动成功==="),
                err -> log.error("===Esb Open 启动失败===", err)
        );
    }

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                deployOpenAgent().ignoreElement(),
                deployTalentService().ignoreElement()
        );
    }

    private Single<String> deployOpenAgent() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config()).setInstances(20);
        return vertx.rxDeployVerticle(OpenAgentVerticle.class.getName(), deploymentOptions);
    }

    private Single<String> deployTalentService() {
        final DeploymentOptions deploymentOptions = new DeploymentOptions()
                .setConfig(config())
                .setWorker(true)
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setInstances(1000);
        return vertx.rxDeployVerticle(TalentServiceVerticle.class.getName(), deploymentOptions);
    }

//    @Override
//    public void start() throws Exception {
//        super.start();
//        OPEN_INJECTOR = Guice.createInjector(new GuiceOpenModule(vertx));
//
//        addProtectedEndpoint("/api/autoLogin/*", "CasClient");
//
//        // task.hengyi.com:9999/api/autoLogin/talent
//        // localhost:9999/api/autoLogin/talent
//        final TalentService talentService = OPEN_INJECTOR.getInstance(TalentService.class);
//        router.get("/api/autoLogin/talent").blockingHandler(talentService::autoLogin);
//
//        // router.post("/texbeedata/addOrder").blockingHandler(this::texbeedataAddOrder);
//        // router.delete("/texbeedata/delOrder/:id").blockingHandler(this::texbeedataDelOrder);
//        // router.post("/texbeedata/closeOrder").blockingHandler(this::texbeedataCloseOrder);
//
//        router.route("/test").blockingHandler(rc -> {
//            rc.response().end("test api qianmeng");
//            System.out.println(rc.getBodyAsString());
//        });
//
//        final JsonObject httpConfig = OPEN_INJECTOR.getInstance(Key.get(JsonObject.class, Names.named("httpConfig")));
//        vertx.createHttpServer()
//                .requestHandler(router::accept)
//                .rxListen(httpConfig.getInteger("port"))
//                .subscribe(
//                        it -> log.info("========开始 ESB SERVICE:" + this.getClass().getName() + "========"),
//                        ex -> log.error("", ex)
//                );
//    }

//    @Override
//    public ConfigFactory getConfigFactory() {
//        return OPEN_INJECTOR.getInstance(ConfigFactory.class);
//    }
//
//    /**
//     * build-in，自动生成 uuid("japp-esb-open")
//     *
//     * @return uuid
//     */
//    @Override
//    public String getAppid() {
//        return "f4166252-f2cd-3b32-8a40-77b80759fc36";
//    }
//
//    /**
//     * build-in，自动生成 uuid({@link #getAppid)
//     * 可修改
//     *
//     * @return uuid
//     */
//    @Override
//    public String getAppsecret() {
//        return "6537ab22-d3f9-3b34-ab71-c3bc2bcc7c79";
//    }

}
