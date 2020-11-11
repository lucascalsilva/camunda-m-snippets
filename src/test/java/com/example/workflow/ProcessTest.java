package com.example.workflow;

import com.example.workflow.delegates.LoggerDelegate;
import com.example.workflow.delegates.ShowVariablesDelegate;
import com.example.workflow.model.Student;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

public class ProcessTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  @Before
  public void setup(){
    init(rule.getProcessEngine());
  }

  @Test
  @Deployment(resources = {"processUserTaskAndMessage.bpmn"})
  public void testHappyPath() {
    /* Registers delegate expression ${showVariablesDelegate} to use the LoggerDelegate */
    Mocks.register("showVariablesDelegate", new LoggerDelegate());

    /* Starts the process instance */
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("ProcessWithTasksAndMessage", withVariables("variable1", "value1",
            "variable2", "value2"));

    assertThat(processInstance).isStarted().hasVariables("variable1", "variable2");

    assertThat(processInstance).isWaitingAt("ExternalTask").isWaitingAt("UserTask").isWaitingAt("MessageTaskA").isWaitingAt("SomeTimerEvent");

    /* Executes timer */
    execute(job());

    /* Completes external task */
    complete(externalTask(), withVariables("variable3", "value3"));

    /* Completes user task */
    complete(task(), withVariables("variable4", "value4"));

    assertThat(processInstance).isEnded();
  }

}
