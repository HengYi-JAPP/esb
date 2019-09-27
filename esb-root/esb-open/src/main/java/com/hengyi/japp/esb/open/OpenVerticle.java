package com.hengyi.japp.esb.open;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.hengyi.japp.esb.core.GuiceModule;
import com.hengyi.japp.esb.open.verticle.OpenAgentVerticle;
import io.vertx.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author jzb 2018-03-18
 */
@Slf4j
public class OpenVerticle extends AbstractVerticle {
    public static String OPEN_MODULE = "esb-open";
    public static Injector OPEN_INJECTOR;

    public static void main(String[] args) {
        final VertxOptions vertxOptions = new VertxOptions()
                .setMaxWorkerExecuteTime(1)
                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
                .setMaxEventLoopExecuteTime(1)
                .setMaxEventLoopExecuteTimeUnit(TimeUnit.MINUTES);
        final Vertx vertx = Vertx.vertx(vertxOptions);
        OPEN_INJECTOR = Guice.createInjector(new GuiceModule(vertx, OPEN_MODULE), new OpenGuiceModule());

        final DeploymentOptions deploymentOptions = new DeploymentOptions();
        vertx.deployVerticle(OpenVerticle.class, deploymentOptions, ar -> {
            if (ar.succeeded()) {
                log.info("===Esb Open[" + ar.result() + "] 启动成功===");
            } else {
                log.error("===Esb Open 启动失败===", ar.cause());
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        deployAgent().<Void>mapEmpty().setHandler(startFuture);
    }

    private Future<String> deployAgent() {
        return Future.future(promise -> {
            final DeploymentOptions deploymentOptions = new DeploymentOptions().setInstances(20);
            vertx.deployVerticle(OpenAgentVerticle.class, deploymentOptions, promise);
        });
    }

//    private Single<String> deployTalentService() {
//        final DeploymentOptions deploymentOptions = new DeploymentOptions()
//                .setConfig(config())
//                .setWorker(true)
//                .setMaxWorkerExecuteTime(1)
//                .setMaxWorkerExecuteTimeUnit(TimeUnit.DAYS)
//                .setInstances(1000);
//        return vertx.rxDeployVerticle(TalentServiceVerticle.class.getName(), deploymentOptions);
//    }

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
