package com.hengyi.japp.esb.oa.command;

import com.github.ixtf.japp.core.J;
import com.hengyi.japp.esb.oa.dto.WorkflowBaseInfoDTO;
import com.hengyi.japp.esb.oa.dto.WorkflowDetailTableInfoDTO;
import com.hengyi.japp.esb.oa.dto.WorkflowMainTableInfoDTO;
import com.hengyi.japp.esb.oa.dto.WorkflowRequestInfoDTO;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ArrayOfWorkflowDetailTableInfo;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ObjectFactory;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author jzb 2019-08-02
 */
@Data
@NoArgsConstructor
public class DoCreateWorkflowRequestCommand implements Serializable {
    private static final ObjectFactory objectFactory = new ObjectFactory();

    private int userid;
    private WorkflowRequestInfoDTO workflowRequestInfo;
    private WorkflowBaseInfoDTO workflowBaseInfo;
    private WorkflowMainTableInfoDTO workflowMainTableInfo;
    private List<WorkflowDetailTableInfoDTO> workflowDetailTableInfos;

    public WorkflowRequestInfo createWorkflowRequestInfo() {
        final WorkflowRequestInfo workflowRequestInfo = this.workflowRequestInfo.createWorkflowRequestInfo();
        workflowRequestInfo.setWorkflowBaseInfo(workflowBaseInfo.createWorkflowBaseInfo());
        workflowRequestInfo.setWorkflowMainTableInfo(workflowMainTableInfo.createWorkflowMainTableInfo());
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
