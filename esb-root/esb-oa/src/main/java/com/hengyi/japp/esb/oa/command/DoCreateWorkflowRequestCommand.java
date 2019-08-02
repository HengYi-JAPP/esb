package com.hengyi.japp.esb.oa.command;

import com.github.ixtf.japp.core.J;
import com.google.common.collect.Lists;
import com.hengyi.japp.esb.oa.dto.*;
import com.hengyi.japp.esb.oa.soap.WorkflowService.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jzb 2019-08-02
 */
@Data
public class DoCreateWorkflowRequestCommand implements Serializable {
    private static final ObjectFactory objectFactory = new ObjectFactory();

    private int userid;
    private WorkflowRequestInfoDTO workflowRequestInfo;
    private WorkflowBaseInfoDTO workflowBaseInfo;
    private WorkflowMainTableInfoDTO workflowMainTableInfo;
    private List<WorkflowDetailTableInfoDTO> workflowDetailTableInfos;

    public static DoCreateWorkflowRequestCommand test() {
        final DoCreateWorkflowRequestCommand command = new DoCreateWorkflowRequestCommand();

        final WorkflowRequestInfoDTO workflowRequestInfo = new WorkflowRequestInfoDTO();
        workflowRequestInfo.setCreatorId("615");
        workflowRequestInfo.setRequestName("流程请求标题-webservice-test");
        workflowRequestInfo.setRequestLevel("0");
        command.setWorkflowRequestInfo(workflowRequestInfo);

        final WorkflowBaseInfoDTO workflowBaseInfo = new WorkflowBaseInfoDTO();
        workflowBaseInfo.setWorkflowId("3984");
        workflowBaseInfo.setWorkflowName("test");
        workflowBaseInfo.setWorkflowTypeName("webservice-test");
        command.setWorkflowBaseInfo(workflowBaseInfo);

        final WorkflowMainTableInfoDTO workflowMainTableInfo = new WorkflowMainTableInfoDTO();
        command.setWorkflowMainTableInfo(workflowMainTableInfo);

        final List<WorkflowDetailTableInfoDTO> workflowDetailTableInfos = Lists.newArrayList();
        final WorkflowDetailTableInfoDTO workflowDetailTableInfo = new WorkflowDetailTableInfoDTO();
        workflowDetailTableInfos.add(workflowDetailTableInfo);

        final List<WorkflowRequestTableRecordDTO> workflowRequestTableRecords = Lists.newArrayList();
        workflowRequestTableRecords.add(new WorkflowRequestTableRecordDTO(Lists.newArrayList(
                new WorkflowRequestTableFieldDTO("ma", "ma1"),
                new WorkflowRequestTableFieldDTO("num", "num1"))));
        workflowRequestTableRecords.add(new WorkflowRequestTableRecordDTO(Lists.newArrayList(
                new WorkflowRequestTableFieldDTO("ma", "ma2"),
                new WorkflowRequestTableFieldDTO("num", "num2"))));
        workflowDetailTableInfo.setWorkflowRequestTableRecords(workflowRequestTableRecords);
        command.setWorkflowDetailTableInfos(workflowDetailTableInfos);

        return command;
    }

    public WorkflowRequestInfo createWorkflowRequestInfo() {
        final WorkflowRequestInfo workflowRequestInfo = this.workflowRequestInfo.createWorkflowRequestInfo();

        final WorkflowBaseInfo workflowBaseInfo = this.workflowBaseInfo.createWorkflowBaseInfo();
        workflowRequestInfo.setWorkflowBaseInfo(objectFactory.createWorkflowRequestInfoWorkflowBaseInfo(workflowBaseInfo));//工作流信息

        final WorkflowMainTableInfo workflowMainTableInfo = this.workflowMainTableInfo.createWorkflowMainTableInfo();
        workflowRequestInfo.setWorkflowMainTableInfo(objectFactory.createWorkflowRequestInfoWorkflowMainTableInfo(workflowMainTableInfo));

        if (J.nonEmpty(workflowDetailTableInfos)) {
            final ArrayOfWorkflowDetailTableInfo arrayOfWorkflowDetailTableInfo = objectFactory.createArrayOfWorkflowDetailTableInfo();
            workflowDetailTableInfos.stream()
                    .map(WorkflowDetailTableInfoDTO::createWorkflowDetailTableInfo)
                    .forEach(arrayOfWorkflowDetailTableInfo.getWorkflowDetailTableInfo()::add);
            workflowRequestInfo.setWorkflowDetailTableInfos(objectFactory.createWorkflowRequestInfoWorkflowDetailTableInfos(arrayOfWorkflowDetailTableInfo));
        }
        return workflowRequestInfo;
    }
}
