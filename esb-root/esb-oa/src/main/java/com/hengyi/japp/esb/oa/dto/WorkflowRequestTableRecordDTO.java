package com.hengyi.japp.esb.oa.dto;

import com.github.ixtf.japp.core.J;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ArrayOfWorkflowRequestTableField;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ObjectFactory;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestTableRecord;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @author jzb 2019-08-02
 */
@Data
@NoArgsConstructor
public class WorkflowRequestTableRecordDTO implements Serializable {
    private static final ObjectFactory objectFactory = new ObjectFactory();

    private List<WorkflowRequestTableFieldDTO> workflowRequestTableFields;

    public WorkflowRequestTableRecordDTO(List<WorkflowRequestTableFieldDTO> workflowRequestTableFields) {
        this.workflowRequestTableFields = workflowRequestTableFields;
    }

    public WorkflowRequestTableRecordDTO(Map<String, String> map) {
        this.workflowRequestTableFields = map.entrySet().stream().map(WorkflowRequestTableFieldDTO::new).collect(toList());
    }

    public WorkflowRequestTableRecord createWorkflowRequestTableRecord() {
        final WorkflowRequestTableRecord workflowRequestTableRecord = objectFactory.createWorkflowRequestTableRecord();
        final ArrayOfWorkflowRequestTableField arrayOfWorkflowRequestTableField = objectFactory.createArrayOfWorkflowRequestTableField();
        if (J.nonEmpty(workflowRequestTableFields)) {
            workflowRequestTableFields.stream()
                    .map(WorkflowRequestTableFieldDTO::createWorkflowRequestTableField)
                    .forEach(arrayOfWorkflowRequestTableField.getWorkflowRequestTableField()::add);
            workflowRequestTableRecord.setWorkflowRequestTableFields(objectFactory.createWorkflowRequestTableRecordWorkflowRequestTableFields(arrayOfWorkflowRequestTableField));
        }
        return workflowRequestTableRecord;
    }
}
