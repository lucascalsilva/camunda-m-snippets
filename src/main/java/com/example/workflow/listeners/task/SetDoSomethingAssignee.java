package com.example.workflow.listeners.task;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.springframework.stereotype.Component;

@Component
public class SetDoSomethingAssignee implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        String previousAssignee = (String) delegateTask.getVariable("previousAssignee");
        if(previousAssignee != null){
            delegateTask.setAssignee(previousAssignee);
        }
    }
}
