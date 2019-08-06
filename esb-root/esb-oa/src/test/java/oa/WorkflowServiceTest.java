package oa;

import com.google.common.collect.Lists;
import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommand;
import com.hengyi.japp.esb.oa.dto.*;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowService;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;

import java.util.List;

/**
 * @author jzb 2019-08-02
 */
public class WorkflowServiceTest {
    private static final WorkflowServicePortType workflowServicePortType = new WorkflowService().getWorkflowServiceHttpPort();

    public static void main(String[] args) {
        final DoCreateWorkflowRequestCommand command = test();
        final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
//        final String s = workflowServiceHttpPort.doCreateWorkflowRequest(workflowRequestInfo, 615);
//        System.out.println(s);
    }

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
}
