package com.example.workflow.delegates;

import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenerateEODFileDelegate implements JavaDelegate {

    private final HistoryService historyService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Date from = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .finishedBefore(from)
                .variableValueEquals("loanReferenceNumber", "").list();

        for(HistoricProcessInstance historicProcessInstance : historicProcessInstances){
            List<HistoricVariableInstance> variableInstances = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(historicProcessInstance.getId()).list();

            //Do something with the variables and with the historic process instance...
        }
    }
}
