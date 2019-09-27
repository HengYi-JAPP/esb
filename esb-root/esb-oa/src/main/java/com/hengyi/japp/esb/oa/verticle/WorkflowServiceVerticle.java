package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.oa.OaUtil;
import com.hengyi.japp.esb.oa.command.DeleteRequestCommand;
import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommand;
import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommandByYunbiao;
import com.hengyi.japp.esb.oa.command.GetWorkflowRequestCommand;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import reactor.core.publisher.Mono;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
public class WorkflowServiceVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        CompositeFuture.all(
                Future.<Void>future(promise -> doCreateWorkflowRequestByYunbiao().completionHandler(promise)),
                Future.<Void>future(promise -> doCreateWorkflowRequest().completionHandler(promise)),
                Future.<Void>future(promise -> getWorkflowRequest().completionHandler(promise)),
                Future.<Void>future(promise -> deleteRequest().completionHandler(promise))
        ).<Void>mapEmpty().setHandler(startFuture);
    }

    private MessageConsumer<String> doCreateWorkflowRequest() {
        final String address = "esb:oa:WorkflowService:doCreateWorkflowRequest";
        return vertx.eventBus().consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "WorkflowService:doCreateWorkflowRequest", address);
            Mono.fromCallable(() -> {
                final DoCreateWorkflowRequestCommand command = MAPPER.readValue(reply.body(), DoCreateWorkflowRequestCommand.class);
                final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
                final WorkflowServicePortType workflowServicePortType = OA_INJECTOR.getInstance(WorkflowServicePortType.class);
                return workflowServicePortType.doCreateWorkflowRequest(workflowRequestInfo, command.getUserid());
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer<String> doCreateWorkflowRequestByYunbiao() {
        final String address = "esb:oa:yunbiao:WorkflowService:doCreateWorkflowRequest";
        return vertx.eventBus().consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "yunbiao:WorkflowService:doCreateWorkflowRequest", address);
            Mono.fromCallable(() -> {
                final DoCreateWorkflowRequestCommandByYunbiao command = MAPPER.readValue(reply.body(), DoCreateWorkflowRequestCommandByYunbiao.class);
                final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
                final WorkflowServicePortType workflowServicePortType = OA_INJECTOR.getInstance(WorkflowServicePortType.class);
                final String data = workflowServicePortType.doCreateWorkflowRequest(workflowRequestInfo, command.getUserid());
                return new JsonObject().put("data", data).encode();
            }).subscribe(it -> {
                apmSuccess(reply, span, it);
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer<String> deleteRequest() {
        final String address = "esb:oa:WorkflowService:deleteRequest";
        return vertx.eventBus().consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "WorkflowService:deleteRequest", address);
            Mono.fromCallable(() -> {
                final DeleteRequestCommand command = MAPPER.readValue(reply.body(), DeleteRequestCommand.class);
                final WorkflowServicePortType workflowServicePortType = OA_INJECTOR.getInstance(WorkflowServicePortType.class);
                return workflowServicePortType.deleteRequest(command.getRequestid(), command.getUserid());
            }).subscribe(it -> {
                apmSuccess(reply, span, "" + it);
                reply.reply(it);
            }, err -> {
                apmError(reply, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer<String> getWorkflowRequest() {
        final String address = "esb:oa:WorkflowService:getWorkflowRequest";
        return vertx.eventBus().consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "WorkflowService:getWorkflowRequest", address);
            Mono.fromCallable(() -> {
                final GetWorkflowRequestCommand command = MAPPER.readValue(reply.body(), GetWorkflowRequestCommand.class);
                final WorkflowServicePortType workflowServicePortType = OA_INJECTOR.getInstance(WorkflowServicePortType.class);
                final WorkflowRequestInfo workflowRequestInfo = workflowServicePortType.getWorkflowRequest(command.getRequestid(), command.getUserid(), command.getFromrequestid());
                return OaUtil.toJsonObject(workflowRequestInfo).encode();
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
