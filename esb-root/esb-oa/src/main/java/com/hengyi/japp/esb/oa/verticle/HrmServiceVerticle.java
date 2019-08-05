package com.hengyi.japp.esb.oa.verticle;

import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommandByYunbiao;
import com.hengyi.japp.esb.oa.soap.HrmService.ArrayOfSubCompanyBean;
import com.hengyi.japp.esb.oa.soap.HrmService.HrmServicePortType;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.reactivex.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static com.hengyi.japp.esb.oa.MainVerticle.GUICE;

/**
 * @author jzb 2019-08-02
 */
@Slf4j
public class HrmServiceVerticle extends AbstractVerticle {

    @Override
    public Completable rxStart() {
        return Completable.mergeArray(
                vertx.eventBus().<String>consumer("esb:oa:HrmService:getHrmSubcompanyInfo", reply -> {
                    final String body = reply.body();
                    Single.fromCallable(() -> {
                        final HrmServicePortType hrmServicePortType = GUICE.getInstance(HrmServicePortType.class);
                        final ArrayOfSubCompanyBean arrayOfSubCompanyBean = hrmServicePortType.getHrmSubcompanyInfo("");
                        return MAPPER.writeValueAsString(arrayOfSubCompanyBean.getSubCompanyBean());
                    }).subscribe(it -> {
                        reply.reply(it);
                    }, err -> {
                        log.error("", err);
                        reply.fail(400, err.getLocalizedMessage());
                    });
                }).rxCompletionHandler(),

                vertx.eventBus().<String>consumer("esb:oa:HrmService:getHrmUserInfo", reply -> {
                    final String body = reply.body();
                    Single.fromCallable(() -> {
                        final DoCreateWorkflowRequestCommandByYunbiao command = MAPPER.readValue(body, DoCreateWorkflowRequestCommandByYunbiao.class);
                        final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
                        final WorkflowServicePortType workflowServicePortType = GUICE.getInstance(WorkflowServicePortType.class);
                        return workflowServicePortType.doCreateWorkflowRequest(workflowRequestInfo, command.getUserid());
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
