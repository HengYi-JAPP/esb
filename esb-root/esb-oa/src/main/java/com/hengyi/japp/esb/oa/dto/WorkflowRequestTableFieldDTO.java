package com.hengyi.japp.esb.oa.dto;

import com.hengyi.japp.esb.oa.soap.WorkflowService.ObjectFactory;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowRequestTableField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author jzb 2019-08-02
 */
@Data
@NoArgsConstructor
public class WorkflowRequestTableFieldDTO implements Serializable {
    private static final ObjectFactory objectFactory = new ObjectFactory();

    private Boolean edit;
    private Boolean view;
    private String fieldName;
    private String fieldValue;

    public WorkflowRequestTableFieldDTO(String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.edit = true;
        this.view = true;
    }

    public WorkflowRequestTableFieldDTO(Map.Entry<String, String> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public WorkflowRequestTableField createWorkflowRequestTableField() {
        final WorkflowRequestTableField workflowRequestTableField = objectFactory.createWorkflowRequestTableField();
        workflowRequestTableField.setFieldName(objectFactory.createWorkflowRequestTableFieldFieldName(fieldName));
        workflowRequestTableField.setFieldValue(objectFactory.createWorkflowRequestTableFieldFieldValue(fieldValue));
        workflowRequestTableField.setEdit(edit);
        workflowRequestTableField.setView(view);
        return workflowRequestTableField;
    }
}
