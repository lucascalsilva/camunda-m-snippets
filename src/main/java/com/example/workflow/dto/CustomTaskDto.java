package com.example.workflow.dto;

import lombok.Data;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;

import java.util.Date;
import java.util.Map;

/* Based in the class org.camunda.bpm.engine.rest.dto.history.HistoricTaskInstanceDto */
@Data
public class CustomTaskDto {

    private String id;
    private String processDefinitionId;
    private String processInstanceId;
    private String executionId;
    private String caseDefinitionKey;
    private String caseDefinitionId;
    private String caseInstanceId;
    private String caseExecutionId;
    private String activityInstanceId;
    private String name;
    private String description;
    private String deleteReason;
    private String owner;
    private String assignee;
    private Date startTime;
    private Date endTime;
    private Long duration;
    private String taskDefinitionKey;
    private int priority;
    private Date due;
    private String parentTaskId;
    private Date followUp;
    private String tenantId;
    private Date removalTime;

    /* Fields that were not existing in the class org.camunda.bpm.engine.rest.dto.history.HistoricTaskInstanceDto */
    private String rootProcessInstanceId;
    private String processType;
    private String businessKey;
    private String referenceNumber;
    private String candidateGroup;
    private String candidateUsers;
    private Map<String, CustomVariableInstanceDto> variables;
    private CustomTaskStatuses taskStatus;

    public static CustomTaskDto fromHistoricTaskInstance(HistoricTaskInstance taskInstance) {
        CustomTaskDto dto = new CustomTaskDto();

        dto.id = taskInstance.getId();
        dto.processType = taskInstance.getProcessDefinitionKey();
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

        /* Added to have a end status */
        if(dto.getEndTime() != null){
            dto.taskStatus = CustomTaskStatuses.COMPLETED;
        }
        else{
            dto.taskStatus = CustomTaskStatuses.RUNNING;
        }

        return dto;
    }

    public static CustomTaskDto fromTask(Task task) {
        CustomTaskDto dto = new CustomTaskDto();

        dto.id = task.getId();
        dto.name = task.getName();
        dto.assignee = task.getAssignee();
        dto.due = task.getDueDate();
        dto.followUp = task.getFollowUpDate();

        dto.description = task.getDescription();
        dto.executionId = task.getExecutionId();
        dto.owner = task.getOwner();
        dto.parentTaskId = task.getParentTaskId();
        dto.priority = task.getPriority();
        dto.processDefinitionId = task.getProcessDefinitionId();
        dto.processInstanceId = task.getProcessInstanceId();
        dto.taskDefinitionKey = task.getTaskDefinitionKey();
        dto.caseDefinitionId = task.getCaseDefinitionId();
        dto.caseExecutionId = task.getCaseExecutionId();
        dto.caseInstanceId = task.getCaseInstanceId();
        dto.tenantId = task.getTenantId();

        return dto;
    }

}
