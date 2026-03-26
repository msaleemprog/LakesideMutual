package com.lakesidemutual.riskmanagement.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.lakesidemutual.policymanagement.api.PolicyRiskProjectionDeleted;
import com.lakesidemutual.policymanagement.api.PolicyRiskProjectionUpdated;
@Component
public class PolicyEventListener {

  private final RiskProjectionStore store;

  public PolicyEventListener(RiskProjectionStore store) {
    this.store = store;
  }

  @EventListener
  public void on(PolicyRiskProjectionUpdated event) {
    store.upsert(event.getPolicy());
  }

  @EventListener
  public void on(PolicyRiskProjectionDeleted event) {
    store.deletePolicy(event.getPolicyId());
  }
}
