package com.example.workflow.delegates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsumeFraudMessagesDelegate implements JavaDelegate {

    private final RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        List<String> loanReferenceNumbers = (List<String>) execution.getVariable("loanReferenceNumbers");

        log.info("Consuming messages from processes with loan reference numbers: {}", loanReferenceNumbers.toString());

        loanReferenceNumbers.forEach(loanReferenceNumber -> {
            runtimeService.createMessageCorrelation("Message")
                    .processInstanceVariableEquals("loanReferenceNumber", loanReferenceNumber)
                    .correlate();
        });
    }
}
