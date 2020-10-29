package com.example.workflow.delegates;

import com.example.workflow.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.impl.json.jackson.JacksonJsonNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class ShowVariablesDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        JacksonJsonNode studentJson = (JacksonJsonNode) execution.getVariable("studentsJSON");
        ArrayList<Student> studentObject = (ArrayList<Student>) execution.getVariable("studentsOBJECT");

        log.info("This is printing a Camunda JSON {}", studentJson.toString());
        log.info("This is printing a deserialized object {}", studentObject.toString());

    }
}
