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
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.MessageConsumer;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.hengyi.japp.esb.core.Util.*;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;

/**
 * @author jzb 2019-08-02
 */
public class WorkflowServiceVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                doCreateWorkflowRequest().rxCompletionHandler(),
                doCreateWorkflowRequestByYunbiao().rxCompletionHandler(),
                getWorkflowRequest().rxCompletionHandler(),
                deleteRequest().rxCompletionHandler()
        );
    }

    private MessageConsumer<String> doCreateWorkflowRequest() {
        final String address = "esb:oa:WorkflowService:doCreateWorkflowRequest";
        return vertx.eventBus().consumer(address, reply -> {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final Span span = initApm(reply, tracer, this, "WorkflowService:doCreateWorkflowRequest", address);
            Single.fromCallable(() -> {
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
            Single.fromCallable(() -> {
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
            Single.fromCallable(() -> {
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
            Single.fromCallable(() -> {
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
