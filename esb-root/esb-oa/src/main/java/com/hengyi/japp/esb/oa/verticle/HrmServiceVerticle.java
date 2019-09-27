package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.oa.soap.HrmService.ArrayOfSubCompanyBean;
import com.hengyi.japp.esb.oa.soap.HrmService.ArrayOfUserBean;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmServicePortType;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class HrmServiceVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        CompositeFuture.all(
                Future.<Void>future(promise -> getHrmSubcompanyInfo().completionHandler(promise)),
                Future.<Void>future(promise -> getHrmUserInfo().completionHandler(promise))
        ).<Void>mapEmpty().setHandler(startFuture);
    }

    private MessageConsumer getHrmSubcompanyInfo() {
        final String address = "esb:oa:HrmService:getHrmSubcompanyInfo";
        return vertx.eventBus().consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "HrmService:getHrmSubcompanyInfo", address);
            Mono.fromCallable(() -> {
                final HrmServicePortType hrmServicePortType = OA_INJECTOR.getInstance(HrmServicePortType.class);
                final ArrayOfSubCompanyBean arrayOfSubCompanyBean = hrmServicePortType.getHrmSubcompanyInfo("");
                return MAPPER.writeValueAsString(arrayOfSubCompanyBean.getSubCompanyBean());
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                log.error("", err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer getHrmUserInfo() {
        final String address = "esb:oa:HrmService:getHrmUserInfo";
        return vertx.eventBus().consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "HrmService:getHrmUserInfo", address);
            Mono.fromCallable(() -> {
                final HrmServicePortType hrmServicePortType = OA_INJECTOR.getInstance(HrmServicePortType.class);
                final ArrayOfUserBean arrayOfUserBean = hrmServicePortType.getHrmUserInfo(null, null, null, null, null, null);
                return MAPPER.writeValueAsString(arrayOfUserBean.getUserBean());
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                log.error("", err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

}
