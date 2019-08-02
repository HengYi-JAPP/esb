package com.hengyi.japp.esb.oa.dto;

import com.hengyi.japp.esb.oa.soap.WorkflowService.ObjectFactory;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jzb 2019-08-02
 */
@Data
public class WorkflowRequestInfoDTO implements Serializable {
    private static final ObjectFactory objectFactory = new ObjectFactory();

    private Boolean canEdit;
    private Boolean canView;
    private String creatorId;
    /**
     * 请求重要级别 0：正常 1：重要 2：紧急
     */
    private String requestLevel;
    private String requestName;
    private String createTime;
    private String currentNodeId;
    private String currentNodeName;
    private String forwardButtonName;
    private String lastOperateTime;
    private String lastOperatorName;
    private String messageType;
    private Boolean mustInputRemark;
    private Boolean needAffirmance;
    private String receiveTime;
    private String rejectButtonName;
    private String remark;
    private String requestId;
    private String status;
    private String subbackButtonName;
    private String submitButtonName;
    private String subnobackButtonName;

    public WorkflowRequestInfo createWorkflowRequestInfo() {
        final WorkflowRequestInfo workflowRequestInfo = objectFactory.createWorkflowRequestInfo();
        workflowRequestInfo.setCanEdit(true);
        workflowRequestInfo.setCanView(true);
        workflowRequestInfo.setRequestLevel(objectFactory.createWorkflowRequestInfoRequestLevel(requestLevel));
        workflowRequestInfo.setCreatorId(objectFactory.createWorkflowRequestInfoCreatorId(creatorId));//创建者ID 创建流程时为必输项
        workflowRequestInfo.setRequestName(objectFactory.createWorkflowRequestInfoRequestName(requestName));//请求标题
        return workflowRequestInfo;
    }
}
