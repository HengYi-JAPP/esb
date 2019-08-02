package com.hengyi.japp.esb.oa.command;

import com.github.ixtf.japp.core.J;
import com.google.common.collect.Lists;
import com.hengyi.japp.esb.oa.dto.*;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.ixtf.japp.core.Constant.MAPPER;
import static java.util.stream.Collectors.toList;

/**
 * @author jzb 2019-08-02
 */
@Data
public class DoCreateWorkflowRequestCommandByYunbiao implements Serializable {
    private int userid;
    private WorkflowRequestInfoDTOByYunbiao workflowRequestInfo;
    private WorkflowBaseInfoDTOByYunbiao workflowBaseInfo;
    private Map<String, String> workflowMainTableInfo;
    private LinkedHashMap<String, List<Map<String, String>>> workflowDetailTableInfoMap;
    private List<Map<String, String>> workflowDetailTableInfo;

    private static WorkflowDetailTableInfoDTO convertWorkflowDetailTableInfoDTO(List<Map<String, String>> list) {
        final List<WorkflowRequestTableRecordDTO> collect = list.stream()
                .map(DoCreateWorkflowRequestCommandByYunbiao::toWorkflowRequestTableFields)
                .map(WorkflowRequestTableRecordDTO::new)
                .collect(toList());
        return new WorkflowDetailTableInfoDTO(collect);
    }

    public static List<WorkflowRequestTableFieldDTO> toWorkflowRequestTableFields(Map<String, String> map) {
        return map.entrySet().stream().map(WorkflowRequestTableFieldDTO::new).collect(toList());
    }

    public WorkflowRequestInfo createWorkflowRequestInfo() {
        final DoCreateWorkflowRequestCommand command = new DoCreateWorkflowRequestCommand();
        command.setUserid(userid);
        command.setWorkflowRequestInfo(convertWorkflowRequestInfo());
        command.setWorkflowBaseInfo(convertWorkflowBaseInfo());
        command.setWorkflowMainTableInfo(convertWorkflowMainTableInfo());
        command.setWorkflowDetailTableInfos(convertWorkflowDetailTableInfos());
        return command.createWorkflowRequestInfo();
    }

    private WorkflowRequestInfoDTO convertWorkflowRequestInfo() {
        return MAPPER.convertValue(workflowRequestInfo, WorkflowRequestInfoDTO.class);
    }

    private WorkflowBaseInfoDTO convertWorkflowBaseInfo() {
        return MAPPER.convertValue(workflowBaseInfo, WorkflowBaseInfoDTO.class);
    }

    private WorkflowMainTableInfoDTO convertWorkflowMainTableInfo() {
        if (J.isEmpty(workflowMainTableInfo)) {
            return new WorkflowMainTableInfoDTO();
        }
        final List<WorkflowRequestTableFieldDTO> workflowRequestTableFields = toWorkflowRequestTableFields(workflowMainTableInfo);
        final WorkflowRequestTableRecordDTO requestRecord = new WorkflowRequestTableRecordDTO(workflowRequestTableFields);
        final List<WorkflowRequestTableRecordDTO> requestRecords = Lists.newArrayList(requestRecord);
        return new WorkflowMainTableInfoDTO(requestRecords);
    }

    private List<WorkflowDetailTableInfoDTO> convertWorkflowDetailTableInfos() {
        if (J.isEmpty(workflowDetailTableInfoMap)) {
            if (J.isEmpty(workflowDetailTableInfo)) {
                return Collections.emptyList();
            }
            return Lists.newArrayList(convertWorkflowDetailTableInfoDTO(workflowDetailTableInfo));
        }
        return workflowDetailTableInfoMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .map(DoCreateWorkflowRequestCommandByYunbiao::convertWorkflowDetailTableInfoDTO)
                .collect(toList());
    }

    @Data
    public static class WorkflowRequestInfoDTOByYunbiao implements Serializable {
        protected Boolean canEdit;
        protected Boolean canView;
        protected String creatorId;
        protected String requestName;
        protected String requestLevel;
    }

    @Data
    public static class WorkflowBaseInfoDTOByYunbiao implements Serializable {
        private String workflowId;
        private String workflowName;
        private String workflowTypeId;
        private String workflowTypeName;
    }
}
