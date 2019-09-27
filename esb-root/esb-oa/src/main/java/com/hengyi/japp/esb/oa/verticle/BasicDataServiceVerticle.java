package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.oa.soap.BasicDataService.BasicDataServicePortType;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class BasicDataServiceVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        CompositeFuture.all(
                Future.<Void>future(promise -> getSubcompanyData().completionHandler(promise)),
                Future.<Void>future(promise -> getDepartmentData().completionHandler(promise)),
                Future.<Void>future(promise -> getSubcompanyData().completionHandler(promise))
        ).<Void>mapEmpty().setHandler(startFuture);
    }

    private MessageConsumer<String> getSubcompanyData() {
        final String address = "esb:oa:BasicDataService:getSubcompanyData";
        return vertx.eventBus().<String>consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "BasicDataService:getSubcompanyData", address);
            Mono.fromCallable(() -> {
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
            Mono.fromCallable(() -> {
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
            Mono.fromCallable(() -> {
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
