package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataServicePortType;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class BasicDataServiceVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                getHrmresourceData().rxCompletionHandler(),
                getDepartmentData().rxCompletionHandler(),
                getSubcompanyData().rxCompletionHandler()
        );
    }

    private MessageConsumer<String> getSubcompanyData() {
        final String address = "esb:oa:BasicDataService:getSubcompanyData";
        return vertx.eventBus().<String>consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "BasicDataService:getSubcompanyData", address);
            Single.fromCallable(() -> {
                final BasicDataServicePortType basicDataServicePortType = OA_INJECTOR.getInstance(BasicDataServicePortType.class);
                return basicDataServicePortType.getSubcompanyData("");
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer<String> getDepartmentData() {
        final String address = "esb:oa:BasicDataService:getDepartmentData";
        return vertx.eventBus().<String>consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "BasicDataService:getDepartmentData", address);
            Single.fromCallable(() -> {
                final BasicDataServicePortType basicDataServicePortType = OA_INJECTOR.getInstance(BasicDataServicePortType.class);
                return basicDataServicePortType.getDepartmentData("");
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer<String> getHrmresourceData() {
        final String address = "esb:oa:BasicDataService:getHrmresourceData";
        return vertx.eventBus().<String>consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "BasicDataService:getHrmresourceData", address);
            Single.fromCallable(() -> {
                final BasicDataServicePortType basicDataServicePortType = OA_INJECTOR.getInstance(BasicDataServicePortType.class);
                return basicDataServicePortType.getHrmresourceData("");
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }
}
