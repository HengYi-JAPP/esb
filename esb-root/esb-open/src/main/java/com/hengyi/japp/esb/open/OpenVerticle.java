package com.hengyi.japp.esb.open;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.hengyi.japp.esb.core.verticle.BaseEsbVerticle;
import com.hengyi.japp.esb.open.application.TalentService;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.pac4j.core.config.ConfigFactory;

/**
 * @author jzb 2018-03-18
 */
public class OpenVerticle extends BaseEsbVerticle {
    public static final String ROOT_PATH = "/home/esb/esb-open";
    public static Injector GUICE;

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(OpenVerticle.class.getName());
    }

    @Override
    public void start() throws Exception {
        super.start();
        GUICE = Guice.createInjector(new GuiceOpenModule(vertx));

        addProtectedEndpoint("/api/autoLogin/*", "CasClient");

        // task.hengyi.com:9999/api/autoLogin/talent
        // localhost:9999/api/autoLogin/talent
        final TalentService talentService = GUICE.getInstance(TalentService.class);
        router.get("/api/autoLogin/talent").blockingHandler(talentService::autoLogin);

        // router.post("/texbeedata/addOrder").blockingHandler(this::texbeedataAddOrder);
        // router.delete("/texbeedata/delOrder/:id").blockingHandler(this::texbeedataDelOrder);
        // router.post("/texbeedata/closeOrder").blockingHandler(this::texbeedataCloseOrder);

        router.route("/test").blockingHandler(rc -> {
            rc.response().end("test api qianmeng");
            System.out.println(rc.getBodyAsString());
        });

        final JsonObject httpConfig = GUICE.getInstance(Key.get(JsonObject.class, Names.named("httpConfig")));
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .rxListen(httpConfig.getInteger("port"))
                .subscribe(
                        it -> log.info("========开始 ESB SERVICE:" + this.getClass().getName() + "========"),
                        ex -> log.error("", ex)
                );
    }

    @Override
    public ConfigFactory getConfigFactory() {
        return GUICE.getInstance(ConfigFactory.class);
    }

    /**
     * build-in，自动生成 uuid("japp-esb-open")
     *
     * @return uuid
     */
    @Override
    public String getAppid() {
        return "f4166252-f2cd-3b32-8a40-77b80759fc36";
    }

    /**
     * build-in，自动生成 uuid({@link #getAppid)
     * 可修改
     *
     * @return uuid
     */
    @Override
    public String getAppsecret() {
        return "6537ab22-d3f9-3b34-ab71-c3bc2bcc7c79";
    }

}
