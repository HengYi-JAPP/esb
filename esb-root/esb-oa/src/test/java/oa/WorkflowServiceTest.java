package oa;

import com.hengyi.japp.esb.oa.command.DoCreateWorkflowRequestCommand;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowService;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowServicePortType;

/**
 * @author jzb 2019-08-02
 */
public class WorkflowServiceTest {
    private static final WorkflowService workflowService = new WorkflowService();
    private static final WorkflowServicePortType workflowServiceHttpPort = workflowService.getWorkflowServiceHttpPort();

    public static void main(String[] args) {
        final DoCreateWorkflowRequestCommand command = DoCreateWorkflowRequestCommand.test();
        final WorkflowRequestInfo workflowRequestInfo = command.createWorkflowRequestInfo();
//        final String s = workflowServiceHttpPort.doCreateWorkflowRequest(workflowRequestInfo, 615);
//        System.out.println(s);
    }
}
