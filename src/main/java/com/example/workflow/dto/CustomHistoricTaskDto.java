package com.example.workflow.dto;

import lombok.Data;
import org.camunda.bpm.engine.history.HistoricTaskInstance;

import java.util.Date;
import java.util.List;

@Data
public class CustomHistoricTaskDto {

    protected String id;
    protected String processDefinitionKey;
    protected String processDefinitionId;
    protected String processInstanceId;
    protected String executionId;
    protected String caseDefinitionKey;
    protected String caseDefinitionId;
    protected String caseInstanceId;
    protected String caseExecutionId;
    protected String activityInstanceId;
    protected String name;
    protected String description;
    protected String deleteReason;
    protected String owner;
    protected String assignee;
    protected Date startTime;
    protected Date endTime;
    protected Long duration;
    protected String taskDefinitionKey;
    protected int priority;
    protected Date due;
    protected String parentTaskId;
    protected Date followUp;
    private String tenantId;
    protected Date removalTime;
    protected String rootProcessInstanceId;
    protected String processType;
    protected String businessKey;
    private String referenceNumber;
    private String candidateGroup;
    private String candidateUsers;
    private List<CustomHistoricVariableInstanceDto> variables;
    private CustomTaskStatuses taskStatus;

    public static CustomHistoricTaskDto fromHistoricTaskInstance(HistoricTaskInstance taskInstance) {
        CustomHistoricTaskDto dto = new CustomHistoricTaskDto();

        dto.id = taskInstance.getId();
        dto.processDefinitionKey = taskInstance.getProcessDefinitionKey();
        dto.processDefinitionId = taskInstance.getProcessDefinitionId();
        dto.processInstanceId = taskInstance.getProcessInstanceId();
        dto.executionId = taskInstance.getExecutionId();
        dto.caseDefinitionKey = taskInstance.getCaseDefinitionKey();
        dto.caseDefinitionId = taskInstance.getCaseDefinitionId();
        dto.caseInstanceId = taskInstance.getCaseInstanceId();
        dto.caseExecutionId = taskInstance.getCaseExecutionId();
        dto.activityInstanceId = taskInstance.getActivityInstanceId();
        dto.name = taskInstance.getName();
        dto.description = taskInstance.getDescription();
        dto.deleteReason = taskInstance.getDeleteReason();
        dto.owner = taskInstance.getOwner();
        dto.assignee = taskInstance.getAssignee();
        dto.startTime = taskInstance.getStartTime();
        dto.endTime = taskInstance.getEndTime();
        dto.duration = taskInstance.getDurationInMillis();
        dto.taskDefinitionKey = taskInstance.getTaskDefinitionKey();
        dto.priority = taskInstance.getPriority();
        dto.due = taskInstance.getDueDate();
        dto.parentTaskId = taskInstance.getParentTaskId();
        dto.followUp = taskInstance.getFollowUpDate();
        dto.tenantId = taskInstance.getTenantId();
        dto.removalTime = taskInstance.getRemovalTime();
        dto.rootProcessInstanceId = taskInstance.getRootProcessInstanceId();

        if(dto.getEndTime() != null){
            dto.taskStatus = CustomTaskStatuses.COMPLETED;
        }
        else{
            dto.taskStatus = CustomTaskStatuses.RUNNING;
        }

        return dto;
    }

}
