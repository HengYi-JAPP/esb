package com.hengyi.japp.esb.oa.dto;

import com.github.ixtf.japp.core.J;
import com.google.common.collect.Lists;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ArrayOfString;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ArrayOfWorkflowRequestTableRecord;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ObjectFactory;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowDetailTableInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author jzb 2019-08-02
 */
@Data
@NoArgsConstructor
public class WorkflowDetailTableInfoDTO implements Serializable {
    private static final ObjectFactory objectFactory = new ObjectFactory();

    private String tableDBName;
    private List<String> tableFieldName;
    private String tableTitle;
    private List<WorkflowRequestTableRecordDTO> workflowRequestTableRecords;

    public WorkflowDetailTableInfoDTO(List<WorkflowRequestTableRecordDTO> workflowRequestTableRecords) {
        this.workflowRequestTableRecords = workflowRequestTableRecords;
    }

    public WorkflowDetailTableInfoDTO(WorkflowRequestTableRecordDTO workflowRequestTableRecord) {
        this(Lists.newArrayList(workflowRequestTableRecord));
    }

    public WorkflowDetailTableInfo createWorkflowDetailTableInfo() {
        final WorkflowDetailTableInfo workflowDetailTableInfo = objectFactory.createWorkflowDetailTableInfo();
        if (J.nonBlank(tableDBName)) {
            workflowDetailTableInfo.setTableDBName(objectFactory.createWorkflowDetailTableInfoTableDBName(tableDBName));
        }
        if (J.nonEmpty(tableFieldName)) {
            final ArrayOfString arrayOfString = objectFactory.createArrayOfString();
            arrayOfString.getString().addAll(tableFieldName);
            workflowDetailTableInfo.setTableFieldName(objectFactory.createWorkflowDetailTableInfoTableFieldName(arrayOfString));
        }
        if (J.nonBlank(tableTitle)) {
            workflowDetailTableInfo.setTableTitle(objectFactory.createWorkflowDetailTableInfoTableTitle(tableTitle));
        }
        if (J.nonEmpty(workflowRequestTableRecords)) {
            final ArrayOfWorkflowRequestTableRecord arrayOfWorkflowRequestTableRecord = objectFactory.createArrayOfWorkflowRequestTableRecord();
            workflowRequestTableRecords.stream()
                    .map(WorkflowRequestTableRecordDTO::createWorkflowRequestTableRecord)
                    .forEach(arrayOfWorkflowRequestTableRecord.getWorkflowRequestTableRecord()::add);
            workflowDetailTableInfo.setWorkflowRequestTableRecords(objectFactory.createWorkflowDetailTableInfoWorkflowRequestTableRecords(arrayOfWorkflowRequestTableRecord));
        }
        return workflowDetailTableInfo;
    }

}
