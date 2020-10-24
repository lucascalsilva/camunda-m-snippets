package com.example.workflow.dto;

import lombok.Data;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceDto;

import java.util.Date;
import java.util.Map;

@Data
public class CustomProcessInstanceDto {

    private String id;
    private String businessKey;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer processDefinitionVersion;
    private Date startTime;
    private Date endTime;
    private Date removalTime;
    private Long durationInMillis;
    private String startUserId;
    private String startActivityId;
    private String deleteReason;
    private String rootProcessInstanceId;
    private String superProcessInstanceId;
    private String superCaseInstanceId;
    private String caseInstanceId;
    private String tenantId;
    private String state;
    private Map<String, CustomVariableInstanceDto> variables;

    public static CustomProcessInstanceDto fromHistoricProcessInstance(HistoricProcessInstance historicProcessInstance) {

        CustomProcessInstanceDto dto = new CustomProcessInstanceDto();

        dto.id = historicProcessInstance.getId();
        dto.businessKey = historicProcessInstance.getBusinessKey();
        dto.processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        dto.processDefinitionKey = historicProcessInstance.getProcessDefinitionKey();
        dto.processDefinitionName = historicProcessInstance.getProcessDefinitionName();
        dto.processDefinitionVersion = historicProcessInstance.getProcessDefinitionVersion();
        dto.startTime = historicProcessInstance.getStartTime();
        dto.endTime = historicProcessInstance.getEndTime();
        dto.removalTime = historicProcessInstance.getRemovalTime();
        dto.durationInMillis = historicProcessInstance.getDurationInMillis();
        dto.startUserId = historicProcessInstance.getStartUserId();
        dto.startActivityId = historicProcessInstance.getStartActivityId();
        dto.deleteReason = historicProcessInstance.getDeleteReason();
        dto.rootProcessInstanceId = historicProcessInstance.getRootProcessInstanceId();
        dto.superProcessInstanceId = historicProcessInstance.getSuperProcessInstanceId();
        dto.superCaseInstanceId = historicProcessInstance.getSuperCaseInstanceId();
        dto.caseInstanceId = historicProcessInstance.getCaseInstanceId();
        dto.tenantId = historicProcessInstance.getTenantId();
        dto.state = historicProcessInstance.getState();

        return dto;
    }
}
