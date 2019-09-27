package com.hengyi.japp.esb.oa;

import com.hengyi.japp.esb.oa.soap.WorkflowService.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Optional;

/**
 * @author jzb 2019-09-15
 */
public class OaUtil {
    public static JsonObject toJsonObject(WorkflowRequestInfo workflowRequestInfo) {
        return new JsonObject().put("createTime", workflowRequestInfo.getCreateTime().getValue())
                .put("creatorId", workflowRequestInfo.getCreatorId().getValue())
                .put("creatorName", workflowRequestInfo.getCreatorName().getValue())
                .put("currentNodeId", workflowRequestInfo.getCurrentNodeId().getValue())
                .put("currentNodeName", workflowRequestInfo.getCurrentNodeName().getValue())
                .put("lastOperateTime", workflowRequestInfo.getLastOperateTime().getValue())
                .put("lastOperatorName", workflowRequestInfo.getLastOperatorName().getValue())
                .put("messageType", workflowRequestInfo.getMessageType().getValue())
                .put("receiveTime", workflowRequestInfo.getReceiveTime().getValue())
                .put("remark", workflowRequestInfo.getRemark().getValue())
                .put("requestId", workflowRequestInfo.getRequestId().getValue())
                .put("requestLevel", workflowRequestInfo.getRequestLevel().getValue())
                .put("requestName", workflowRequestInfo.getRequestName().getValue())
                .put("needAffirmance", workflowRequestInfo.isMustInputRemark())
                .put("status", workflowRequestInfo.getStatus().getValue())
                .put("workflowMainTableInfo", toJsonObject(workflowRequestInfo.getWorkflowMainTableInfo().getValue()))
                .put("workflowBaseInfo", toJsonObject(workflowRequestInfo.getWorkflowBaseInfo().getValue()));
    }

    public static JsonObject toJsonObject(WorkflowMainTableInfo workflowMainTableInfo) {
        final JsonArray requestRecords = new JsonArray();
        Optional.ofNullable(workflowMainTableInfo.getRequestRecords().getValue())
                .map(ArrayOfWorkflowRequestTableRecord::getWorkflowRequestTableRecord)
                .stream().flatMap(Collection::stream)
                .map(OaUtil::toJsonObject)
                .forEach(requestRecords::add);
        return new JsonObject().put("tableDBName", workflowMainTableInfo.getTableDBName().getValue())
                .put("requestRecords", requestRecords);
    }

    private static JsonObject toJsonObject(WorkflowRequestTableRecord workflowRequestTableRecord) {
        final JsonObject ret = new JsonObject();
        Optional.ofNullable(workflowRequestTableRecord.getWorkflowRequestTableFields().getValue())
                .map(ArrayOfWorkflowRequestTableField::getWorkflowRequestTableField)
                .stream().flatMap(Collection::stream)
                .forEach(workflowRequestTableField -> ret.put(workflowRequestTableField.getFieldName().getValue(), workflowRequestTableField.getFieldValue().getValue()));
        return ret;
    }

    public static JsonObject toJsonObject(WorkflowBaseInfo workflowBaseInfo) {
        return new JsonObject().put("workflowId", workflowBaseInfo.getWorkflowId().getValue())
                .put("workflowName", workflowBaseInfo.getWorkflowName().getValue())
                .put("workflowTypeId", workflowBaseInfo.getWorkflowTypeId().getValue())
                .put("workflowTypeName", workflowBaseInfo.getWorkflowTypeName().getValue());
    }
}
