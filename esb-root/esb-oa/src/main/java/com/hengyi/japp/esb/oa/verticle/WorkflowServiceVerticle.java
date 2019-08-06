package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.core.apm.RCTextMapExtractAdapter;
import com.hengyi.japp.esb.oa.command.DeleteRequestCommand;
import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommand;
import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommandByYunbiao;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageConsumer;
import lombok.extern.slf4j.Slf4j;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.hengyi.japp.esb.oa.OaVerticle.OA_INJECTOR;
import static io.opentracing.propagation.Format.Builtin.TEXT_MAP;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class WorkflowServiceVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                doCreateWorkflowRequest().rxCompletionHandler(),
                deleteRequest().rxCompletionHandler(),
                doCreateWorkflowRequestByYunbiao().rxCompletionHandler()
        );
    }

    private MessageConsumer<String> doCreateWorkflowRequest() {
        final String address = "esb:oa:WorkflowService:doCreateWorkflowRequest";
        return vertx.eventBus().consumer(address, reply -> {
            final Span span = initApm(reply, "WorkflowService:doCreateWorkflowRequest", address);
            Single.fromCallable(() -> {
                final DoCreateWorkflowRequestCommand command = MAPPER.readValue(reply.body(), DoCreateWorkflowRequestCommand.class);
                final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
                final WorkflowServicePortType workflowServicePortType = OA_INJECTOR.getInstance(WorkflowServicePortType.class);
                return workflowServicePortType.doCreateWorkflowRequest(workflowRequestInfo, command.getUserid());
            }).subscribe(it -> {
                apmSuccess(span, reply, it);
                reply.reply(it);
            }, err -> {
                apmError(address, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer<String> deleteRequest() {
        final String address = "esb:oa:WorkflowService:deleteRequest";
        return vertx.eventBus().consumer(address, reply -> {
            final Span span = initApm(reply, "WorkflowService:deleteRequest", address);
            Single.fromCallable(() -> {
                final DeleteRequestCommand command = MAPPER.readValue(reply.body(), DeleteRequestCommand.class);
                final WorkflowServicePortType workflowServicePortType = OA_INJECTOR.getInstance(WorkflowServicePortType.class);
                return workflowServicePortType.deleteRequest(command.getRequestid(), command.getUserid());
            }).subscribe(it -> {
                apmSuccess(span, reply, "" + it);
                reply.reply(it);
            }, err -> {
                apmError(address, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private MessageConsumer<String> doCreateWorkflowRequestByYunbiao() {
        final String address = "esb:oa:yunbiao:WorkflowService:doCreateWorkflowRequest";
        return vertx.eventBus().consumer(address, reply -> {
            final Span span = initApm(reply, "yunbiao:WorkflowService:doCreateWorkflowRequest", address);
            Single.fromCallable(() -> {
                final DoCreateWorkflowRequestCommandByYunbiao command = MAPPER.readValue(reply.body(), DoCreateWorkflowRequestCommandByYunbiao.class);
                final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
                final WorkflowServicePortType workflowServicePortType = OA_INJECTOR.getInstance(WorkflowServicePortType.class);
                final String data = workflowServicePortType.doCreateWorkflowRequest(workflowRequestInfo, command.getUserid());
                return new JsonObject().put("data", data).encode();
            }).subscribe(it -> {
                apmSuccess(span, reply, it);
                reply.reply(it);
            }, err -> {
                apmError(address, span, err);
                reply.fail(400, err.getLocalizedMessage());
            });
        });
    }

    private Span initApm(Message reply, String operationName, String address) {
        try {
            final Tracer tracer = OA_INJECTOR.getInstance(Tracer.class);
            final SpanContext spanContext = tracer.extract(TEXT_MAP, new RCTextMapExtractAdapter(reply));
            final Tracer.SpanBuilder spanBuilder = tracer.buildSpan(operationName)
                    .withTag(Tags.COMPONENT, this.getClass().getSimpleName())
                    .withTag(Tags.SPAN_KIND, Tags.SPAN_KIND_CONSUMER)
                    .withTag(Tags.MESSAGE_BUS_DESTINATION, address);
            if (spanContext != null) {
                spanBuilder.asChildOf(spanContext);
            }
            return spanBuilder.start();
        } catch (Throwable e) {
            log.error("initApm", e);
            return null;
        }
    }

    private void apmSuccess(Span span, Message reply, String message) {
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, false);
        span.log(message);
        span.finish();
    }

    private void apmError(String address, Span span, Throwable err) {
        log.error(address, err);
        if (span == null) {
            return;
        }
        span.setTag(Tags.ERROR, true);
        span.log(err.getMessage());
        span.finish();
    }

}
