package com.lakesidemutual.policymanagement.infrastructure;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class RiskManagementEventPublisher {

  private final ApplicationEventPublisher publisher;

  public RiskManagementEventPublisher(ApplicationEventPublisher publisher) {
    this.publisher = publisher;
  }

  public void publish(Object event) {
    publisher.publishEvent(event);
  }
}
