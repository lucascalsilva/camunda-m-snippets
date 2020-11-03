package com.example.workflow.controllers;

import com.example.workflow.dto.CustomProcessInstanceDto;
import com.example.workflow.dto.CustomTaskDto;
import com.example.workflow.dto.CustomVariableInstanceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.rest.dto.history.HistoricProcessInstanceQueryDto;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceQueryDto;
import org.camunda.bpm.engine.rest.dto.task.TaskQueryDto;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.task.Task;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.workflow.util.CustomControllerHelper.*;

@RestController
@RequestMapping("/custom")
@RequiredArgsConstructor
public class CustomCamundaProcessController {

    private final HistoryService historyService;
    private final ProcessEngine processEngine;
    private final ObjectMapper objectMapper;

    /* Supports all filters as in the following documentation https://docs.camunda.org/manual/7.14/reference/rest/history/process-instance/get-process-instance-query/ */
    @GetMapping("/audit/process-instance")
    public List<CustomProcessInstanceDto> getAllProcesses(@RequestParam Map<String, String> queryParameters,
                                                          @RequestParam(required = false) Integer firstResult, @RequestParam(required = false) Integer maxResults) {
        HistoricProcessInstanceQuery query = new HistoricProcessInstanceQueryDto(objectMapper, convertMap(queryParameters)).toQuery(processEngine);

        List<HistoricProcessInstance> historicProcessInstances = runQuery(query, firstResult, maxResults);

        return historicProcessInstances.stream().map(this::mapProcessInstanceToCustomProcessInstanceDto).collect(Collectors.toList());
    }

    private CustomProcessInstanceDto mapProcessInstanceToCustomProcessInstanceDto(HistoricProcessInstance historicProcessInstance) {
        CustomProcessInstanceDto customProcessInstanceDto = CustomProcessInstanceDto.fromHistoricProcessInstance(historicProcessInstance);

        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                .executionIdIn(customProcessInstanceDto.getId()).list();

        Map<String, CustomVariableInstanceDto> customVariableInstances = historicVariableInstances.stream()
                .collect(Collectors.toMap(HistoricVariableInstance::getName, CustomVariableInstanceDto::fromHistoricVariableInstance));

        customProcessInstanceDto.setVariables(customVariableInstances);

        return customProcessInstanceDto;
    }
}
