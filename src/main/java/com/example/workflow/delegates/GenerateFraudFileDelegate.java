package com.example.workflow.delegates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateFraudFileDelegate implements JavaDelegate {

    private final RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        List<EventSubscription> eventSubscriptions = runtimeService.createEventSubscriptionQuery().eventName("Message").eventType("message").list();

        List<String> loanReferenceNumbers = eventSubscriptions.stream().map(eventSubscription -> {
            log.info("Found an event subscription: {}", eventSubscription.toString());
            Map<String, Object> variables = runtimeService.getVariables(eventSubscription.getProcessInstanceId());
            log.info("For the event subscription {} the following variables were found {}", eventSubscription.getId(), variables);

            return (String) variables.get("loanReferenceNumber");
        }).filter(Objects::nonNull).collect(Collectors.toList());

        ObjectValue loanReferenceNumbersObj = Variables.objectValue(loanReferenceNumbers)
                .serializationDataFormat(Variables.SerializationDataFormats.JSON).create();
        execution.setVariable("loanReferenceNumbers", loanReferenceNumbersObj);

    }
}
