package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommand;
import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommandByYunbiao;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.hengyi.japp.esb.oa.MainVerticle.GUICE;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class WorkflowServiceVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                vertx.eventBus().<String>consumer("esb:oa:WorkflowService:doCreateWorkflowRequest", reply -> {
                    final String body = reply.body();
                    Single.fromCallable(() -> {
                        final DoCreateWorkflowRequestCommand command = MAPPER.readValue(body, DoCreateWorkflowRequestCommand.class);
                        final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
                        final WorkflowServicePortType workflowServicePortType = GUICE.getInstance(WorkflowServicePortType.class);
                        return workflowServicePortType.doCreateWorkflowRequest(workflowRequestInfo, command.getUserid());
                    }).subscribe(it -> {
                        reply.reply(it);
                    }, err -> {
                        log.error("", err);
                        reply.fail(400, err.getLocalizedMessage());
                    });
                }).rxCompletionHandler(),

                vertx.eventBus().<String>consumer("esb:oa:yunbiao:WorkflowService:doCreateWorkflowRequest", reply -> {
                    final String body = reply.body();
                    Single.fromCallable(() -> {
                        final DoCreateWorkflowRequestCommandByYunbiao command = MAPPER.readValue(body, DoCreateWorkflowRequestCommandByYunbiao.class);
                        final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
                        final WorkflowServicePortType workflowServicePortType = GUICE.getInstance(WorkflowServicePortType.class);
                        final String data = workflowServicePortType.doCreateWorkflowRequest(workflowRequestInfo, command.getUserid());
                        return new JsonObject().put("data", data).encode();
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
