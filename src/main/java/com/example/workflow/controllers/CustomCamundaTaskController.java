package com.example.workflow.controllers;

import com.example.workflow.dto.CustomVariableInstanceDto;
import com.example.workflow.dto.CustomTaskDto;
import com.example.workflow.dto.CustomTaskTypes;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.*;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.dto.history.HistoricExternalTaskLogQueryDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricTaskInstanceQueryDto;
import org.camunda.bpm.engine.rest.dto.task.TaskQueryDto;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.workflow.util.CustomControllerHelper.*;

@RestController
@RequestMapping("/custom")
@RequiredArgsConstructor
public class CustomCamundaTaskController {

    private final HistoryService historyService;
    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final RepositoryService repositoryService;
    private final ProcessEngine processEngine;
    private final ObjectMapper objectMapper;

    /* Queries running user tasks based on the parameters */
    @GetMapping("/task")
    public List<CustomTaskDto> getRunningTasks(@RequestParam Map<String, String> queryParameters,
                                               @RequestParam(required = false) Integer firstResult, @RequestParam(required = false) Integer maxResults) {
        TaskQuery query = new TaskQueryDto(objectMapper, convertMap(queryParameters)).toQuery(processEngine);

        List<Task> taskList = runQuery(query, firstResult, maxResults);

        return taskList.stream().map(this::mapTaskToCustomTaskDto).collect(Collectors.toList());
    }

    /* Queries running and finished user tasks and external tasks */
    @GetMapping("/audit/task")
    public List<CustomTaskDto> getAllTasks(@RequestParam Map<String, String> queryParameters, @RequestParam(required = true) CustomTaskTypes queryTaskType,
                                           @RequestParam(required = false) Integer firstResult, @RequestParam(required = false) Integer maxResults){

        List<CustomTaskDto> result = new ArrayList<>();

        if(queryTaskType.equals(CustomTaskTypes.ALL) || queryTaskType.equals(CustomTaskTypes.USER_TASK)) {
            HistoricTaskInstanceQuery query = new HistoricTaskInstanceQueryDto(objectMapper, convertMap(queryParameters)).toQuery(processEngine);
            List<HistoricTaskInstance> historicTaskInstances = runQuery(query, firstResult, maxResults);
            List<CustomTaskDto> userTaskList = historicTaskInstances.stream().map(this::mapUserTaskToCustomTaskDto).collect(Collectors.toList());

            result.addAll(userTaskList);
        }

        if(queryTaskType.equals(CustomTaskTypes.ALL) || queryTaskType.equals(CustomTaskTypes.EXTERNAL_TASK)) {
            HistoricExternalTaskLogQuery query = new HistoricExternalTaskLogQueryDto(objectMapper, convertMap(queryParameters)).toQuery(processEngine);
            List<HistoricExternalTaskLog> historicExternalTaskLogs = runQuery(query, firstResult, maxResults);
            List<CustomTaskDto> externalTaskList = historicExternalTaskLogs.stream().filter(HistoricExternalTaskLog::isCreationLog)
                    .map(historicExternalTaskLog -> mapExternalTaskToCustomTaskDto(historicExternalTaskLog, historicExternalTaskLogs))
                    .collect(Collectors.toList());

            result.addAll(externalTaskList);
        }

        return result;
    }

    public CustomTaskDto mapTaskToCustomTaskDto(Task task){
        CustomTaskDto customTaskDto = CustomTaskDto.fromTask(task);

        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        customTaskDto.setCandidateGroups(identityLinksForTask.stream().map(IdentityLink::getGroupId).filter(Objects::nonNull).collect(Collectors.toList()));
        customTaskDto.setCandidateUsers(identityLinksForTask.stream().map(IdentityLink::getUserId).filter(Objects::nonNull).collect(Collectors.toList()));

        List<VariableInstance> variableInstances = runtimeService.createVariableInstanceQuery()
                .executionIdIn(customTaskDto.getProcessInstanceId()).list();

        Map<String, CustomVariableInstanceDto> customVariableInstances = variableInstances.stream()
                .collect(Collectors.toMap(VariableInstance::getName, CustomVariableInstanceDto::fromVariableInstance));

        customTaskDto.setVariables(customVariableInstances);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(customTaskDto.getProcessDefinitionId()).singleResult();

        if(processDefinition.getKey() != null){
            customTaskDto.setProcessDefinitionKey(processDefinition.getKey());
        }

        Optional<String> processType = variableInstances.stream().filter(variableInstance ->
                variableInstance.getName().equalsIgnoreCase("processType")).map(variableInstance -> {
            return (String) variableInstance.getValue();
        }).findFirst();

        customTaskDto.setProcessType(processType.orElse(customTaskDto.getProcessDefinitionKey()));

        return customTaskDto;
    }

    public CustomTaskDto mapUserTaskToCustomTaskDto(HistoricTaskInstance historicTaskInstance){
        CustomTaskDto customTaskDto = CustomTaskDto.fromHistoricTaskInstance(historicTaskInstance);

        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                .executionIdIn(customTaskDto.getProcessInstanceId()).list();

        Map<String, CustomVariableInstanceDto> customVariableInstances = historicVariableInstances.stream()
                .collect(Collectors.toMap(HistoricVariableInstance::getName, CustomVariableInstanceDto::fromHistoricVariableInstance));

        customTaskDto.setVariables(customVariableInstances);

        return customTaskDto;
    }

    public CustomTaskDto mapExternalTaskToCustomTaskDto(HistoricExternalTaskLog startHistoricExternalTaskLog,
                                                        List<HistoricExternalTaskLog> historicExternalTaskLogs){
        HistoricExternalTaskLog endHistoricExternalTaskLog = historicExternalTaskLogs.stream()
                .filter(historicExternalTaskLog -> ( historicExternalTaskLog.isSuccessLog() || historicExternalTaskLog.isDeletionLog())
                        && historicExternalTaskLog.getExternalTaskId().equalsIgnoreCase(startHistoricExternalTaskLog.getExternalTaskId())).findFirst()
                .orElse(null);

        CustomTaskDto customTaskDto = CustomTaskDto.fromHistoricExternalTaskLog(startHistoricExternalTaskLog, endHistoricExternalTaskLog);
        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                .executionIdIn(customTaskDto.getProcessInstanceId()).list();

        Map<String, CustomVariableInstanceDto> customVariableInstances = historicVariableInstances.stream()
                .collect(Collectors.toMap(HistoricVariableInstance::getName, CustomVariableInstanceDto::fromHistoricVariableInstance));

        customTaskDto.setVariables(customVariableInstances);

        return customTaskDto;
    }
}
