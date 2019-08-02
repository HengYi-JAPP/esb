package com.hengyi.japp.esb.oa.dto;

import com.github.ixtf.japp.core.J;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ObjectFactory;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowBaseInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 工作流信息
 *
 * @author jzb 2019-08-02
 */
@Data
public class WorkflowBaseInfoDTO implements Serializable {
    private static final ObjectFactory objectFactory = new ObjectFactory();
    /**
     * 流程ID
     */
    private String workflowId;
    private String workflowName;
    private String workflowTypeId;
    private String workflowTypeName;

    public WorkflowBaseInfo createWorkflowBaseInfo() {
        final WorkflowBaseInfo workflowBaseInfo = objectFactory.createWorkflowBaseInfo();
        if (J.nonBlank(workflowId)) {
            workflowBaseInfo.setWorkflowId(objectFactory.createWorkflowBaseInfoWorkflowId(workflowId));
        }
        if (J.nonBlank(workflowName)) {
            workflowBaseInfo.setWorkflowName(objectFactory.createWorkflowBaseInfoWorkflowName(workflowName));
        }
        if (J.nonBlank(workflowTypeId)) {
            workflowBaseInfo.setWorkflowTypeId(objectFactory.createWorkflowBaseInfoWorkflowTypeId(workflowTypeId));
        }
        if (J.nonBlank(workflowTypeName)) {
            workflowBaseInfo.setWorkflowTypeName(objectFactory.createWorkflowBaseInfoWorkflowTypeName(workflowTypeName));
        }
        return workflowBaseInfo;
    }
}
