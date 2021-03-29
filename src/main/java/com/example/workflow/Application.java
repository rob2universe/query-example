package com.example.workflow;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@Slf4j
@SpringBootApplication
@EnableProcessApplication
public class Application {

  @Autowired
  private RuntimeService runtimeService;

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }

//  @EventListener
  public void processPostDeploy(PostDeployEvent event) {

    String processDefinitionKey = "QueryTestProcess";
    // Status: New C1
    ProcessInstance piNewC1 = runtimeService.startProcessInstanceByKey(processDefinitionKey, Map.of("company","C1"));
    log.info("piNewC1 {}",piNewC1.getProcessInstanceId());

    // Status: New C2
    ProcessInstance piNewC2 = runtimeService.startProcessInstanceByKey(processDefinitionKey, Map.of("company","C2"));
    log.info("piNewC2 {}",piNewC2.getProcessInstanceId());

    // Status: New C3
    ProcessInstance piNewC3 = runtimeService.startProcessInstanceByKey(processDefinitionKey, Map.of("company","C3"));
    log.info("piNewC3 {}",piNewC3.getProcessInstanceId());

    // Status: In progress C1
    ProcessInstance piInProgressC1 = runtimeService.createProcessInstanceByKey(processDefinitionKey)
            .startBeforeActivity("INPROGRESSEvent").setVariable("company","C1").execute();
    log.info("piInProgressC1 {}",piInProgressC1.getProcessInstanceId());


  }

}