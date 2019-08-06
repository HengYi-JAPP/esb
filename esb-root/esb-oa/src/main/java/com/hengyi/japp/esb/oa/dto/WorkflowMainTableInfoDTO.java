package com.hengyi.japp.esb.oa.dto;

import com.github.ixtf.japp.core.J;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ArrayOfWorkflowRequestTableRecord;
import com.hengyi.japp.esb.oa.soap.WorkflowService.ObjectFactory;
import com.hengyi.japp.esb.oa.soap.WorkflowService.WorkflowMainTableInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.JAXBElement;
import java.io.Serializable;
import java.util.List;

/**
 * @author jzb 2019-08-02
 */
@Data
@NoArgsConstructor
public class WorkflowMainTableInfoDTO implements Serializable {
    private static final ObjectFactory objectFactory = new ObjectFactory();

    private String tableDBName;
    private List<WorkflowRequestTableRecordDTO> requestRecords;

    public WorkflowMainTableInfoDTO(List<WorkflowRequestTableRecordDTO> requestRecords) {
        this.requestRecords = requestRecords;
    }

    public JAXBElement<WorkflowMainTableInfo> createWorkflowMainTableInfo() {
        final WorkflowMainTableInfo workflowMainTableInfo = objectFactory.createWorkflowMainTableInfo();
        if (J.nonBlank(tableDBName)) {
            workflowMainTableInfo.setTableDBName(objectFactory.createWorkflowMainTableInfoTableDBName(tableDBName));
        }
        if (J.nonEmpty(requestRecords)) {
            final ArrayOfWorkflowRequestTableRecord arrayOfWorkflowRequestTableRecord = objectFactory.createArrayOfWorkflowRequestTableRecord();
            requestRecords.stream()
                    .map(WorkflowRequestTableRecordDTO::createWorkflowRequestTableRecord)
                    .forEach(arrayOfWorkflowRequestTableRecord.getWorkflowRequestTableRecord()::add);
            workflowMainTableInfo.setRequestRecords(objectFactory.createWorkflowMainTableInfoRequestRecords(arrayOfWorkflowRequestTableRecord));
        }
        return objectFactory.createWorkflowRequestInfoWorkflowMainTableInfo(workflowMainTableInfo);
    }
}
