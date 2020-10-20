package com.example.workflow.controllers;

import com.example.workflow.dto.CustomHistoricVariableInstanceDto;
import com.example.workflow.dto.CustomHistoricTaskDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.rest.dto.history.HistoricTaskInstanceQueryDto;
import org.camunda.bpm.engine.rest.dto.task.TaskQueryDto;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/custom/task")
@RequiredArgsConstructor
public class CustomCamundaTaskController {

    private final HistoryService historyService;
    private final ProcessEngine processEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public List<CustomHistoricTaskDto> getAllTasks(@RequestParam Map<String, String> queryParameters,
                                                   @RequestParam(required = false) Integer firstResult, @RequestParam(required = false) Integer maxResults){
        MultivaluedMap<String, String> multiValueMap = convertMap(queryParameters);
        HistoricTaskInstanceQueryDto historicTaskInstanceQueryDto = new HistoricTaskInstanceQueryDto(objectMapper, multiValueMap);
        HistoricTaskInstanceQuery query = historicTaskInstanceQueryDto.toQuery(processEngine);

        List<HistoricTaskInstance> historicTaskInstances;
        if (firstResult != null || maxResults != null) {
            historicTaskInstances = executePaginatedQuery(query, firstResult, maxResults);
        } else {
            historicTaskInstances = query.list();
        }

        return historicTaskInstances.stream().map(historicTaskInstance -> {
            CustomHistoricTaskDto customTaskDto = CustomHistoricTaskDto.fromHistoricTaskInstance(historicTaskInstance);
            List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                    .executionIdIn(customTaskDto.getExecutionId()).list();

            List<CustomHistoricVariableInstanceDto> customHistoricVariableInstanceDtoList = historicVariableInstances.stream()
                    .map(CustomHistoricVariableInstanceDto::fromHistoricVariableInstance).collect(Collectors.toList());


            customTaskDto.setVariables(customHistoricVariableInstanceDtoList);

            return customTaskDto;

        }).collect(Collectors.toList());
    }

    private List<HistoricTaskInstance> executePaginatedQuery(HistoricTaskInstanceQuery query, Integer firstResult, Integer maxResults) {
        if (firstResult == null) {
            firstResult = 0;
        }
        if (maxResults == null) {
            maxResults = Integer.MAX_VALUE;
        }
        return query.listPage(firstResult, maxResults);
    }

    public static MultivaluedMap<String, String> convertMap(Map<String, String> map){
        MultivaluedMap<String, String> multiValueMap = new MultivaluedHashMap<String, String>();

        map.forEach(multiValueMap::add);

        return multiValueMap;
    }
}
