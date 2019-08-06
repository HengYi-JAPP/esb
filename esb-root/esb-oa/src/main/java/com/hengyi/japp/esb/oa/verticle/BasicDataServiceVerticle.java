package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataServicePortType;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class BasicDataServiceVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                vertx.eventBus().<String>consumer("esb:oa:BasicDataService:getHrmresourceData", reply -> {
                    final String body = reply.body();
                    Single.fromCallable(() -> {
                        final BasicDataServicePortType basicDataServicePortType = OA_INJECTOR.getInstance(BasicDataServicePortType.class);
                        return basicDataServicePortType.getHrmresourceData("");
                    }).subscribe(it -> {
                        reply.reply(it);
                    }, err -> {
                        log.error("", err);
                        reply.fail(400, err.getLocalizedMessage());
                    });
                }).rxCompletionHandler(),

                vertx.eventBus().<String>consumer("esb:oa:BasicDataService:getDepartmentData", reply -> {
                    Single.fromCallable(() -> {
                        final BasicDataServicePortType basicDataServicePortType = OA_INJECTOR.getInstance(BasicDataServicePortType.class);
                        return basicDataServicePortType.getDepartmentData("");
                    }).subscribe(it -> {
                        reply.reply(it);
                    }, err -> {
                        log.error("", err);
                        reply.fail(400, err.getLocalizedMessage());
                    });
                }).rxCompletionHandler(),

                vertx.eventBus().<String>consumer("esb:oa:BasicDataService:getSubcompanyData", reply -> {
                    Single.fromCallable(() -> {
                        final BasicDataServicePortType basicDataServicePortType = OA_INJECTOR.getInstance(BasicDataServicePortType.class);
                        return basicDataServicePortType.getSubcompanyData("");
                    }).subscribe(it -> {
                        reply.reply(it);
                    }, err -> {
                        log.error("", err);
                        reply.fail(400, err.getLocalizedMessage());
                    });
                }).rxCompletionHandler()
        );
    }
}
