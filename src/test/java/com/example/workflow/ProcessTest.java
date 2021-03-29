package com.example.workflow;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Deployment(resources = "process.bpmn")
public class ProcessTest {

    @Rule
    public ProcessEngineRule engine = new ProcessEngineRule();
    public static final String PDKEY = "QueryTestProcess";

    @Test
    public void shouldExecuteHappyPath() {

        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PDKEY);

        assertThat(processInstance).isStarted().variables().containsEntry("status", "New");
        complete(task());

        assertThat(processInstance).isWaitingAt("DoSoemthingElseTask").variables().containsEntry("status", "In Progress");
        complete(task());
        assertThat(processInstance).isEnded().variables().containsEntry("status", "Completed");
    }


    @Test
    public void testQueryStatusNewAndInProgress() {

        // Status: New
        ProcessInstance piNew = runtimeService().startProcessInstanceByKey(PDKEY);
        assertThat(piNew).isStarted().variables().containsEntry("status", "New");
        // Status: In progress
        ProcessInstance piInProgress = runtimeService().createProcessInstanceByKey(PDKEY)
                .startBeforeActivity("INPROGRESSEvent")
                .execute();
        assertThat(piInProgress).isStarted().variables().containsEntry("status", "In Progress");


        List<ProcessInstance> pis = processInstanceQuery().or()
                .variableValueEquals("status", "New")
                .variableValueEquals("status", "In Progress")
                .endOr()
                .list();
        Assertions.assertEquals(2, pis.size());
    }

    @Test
    public void testQueryStatusNewC1andC2() {

        // Status: New C1
        ProcessInstance piNewC1 = runtimeService().startProcessInstanceByKey(PDKEY,
                withVariables("company","C1"));
        assertThat(piNewC1).isStarted().variables().containsEntry("status", "New").containsEntry("company","C1");        // Status: New C1

        // Status: New C2
        ProcessInstance piNewC2 = runtimeService().startProcessInstanceByKey(PDKEY,
                withVariables("company","C2"));
        assertThat(piNewC2).isStarted().variables().containsEntry("status", "New").containsEntry("company","C2");

        // Status: New C3
        ProcessInstance piNewC3 = runtimeService().startProcessInstanceByKey(PDKEY,
                withVariables("company","C3"));
        assertThat(piNewC3).isStarted().variables().containsEntry("status", "New").containsEntry("company","C3");

        // Status: In progress C1
        ProcessInstance piInProgressC1 = runtimeService().createProcessInstanceByKey(PDKEY)
                .startBeforeActivity("INPROGRESSEvent").setVariable("company","C1").execute();
        assertThat(piInProgressC1).isStarted().variables()
                .containsEntry("status", "In Progress").containsEntry("company","C1");

//        List pis = processInstanceQuery()
        List<ProcessInstance> pis = engine.getRuntimeService().createProcessInstanceQuery()
                .variableValueEquals("status", "New")
                .or()
                .variableValueEquals("company","C1")
                .variableValueEquals("company","C2")
                .endOr()
                .list();
        Assertions.assertEquals(2, pis.size());
    }

}
