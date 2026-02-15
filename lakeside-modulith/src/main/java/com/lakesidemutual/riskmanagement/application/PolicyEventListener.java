package com.lakesidemutual.riskmanagement.application;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.lakesidemutual.policymanagement.domain.policy.DeletePolicyEvent;
import com.lakesidemutual.policymanagement.domain.policy.UpdatePolicyEvent;

@Component
public class PolicyEventListener {

  private final RiskProjectionStore store;

  public PolicyEventListener(RiskProjectionStore store) {
    this.store = store;
  }

  @EventListener
  public void on(UpdatePolicyEvent event) {
    store.upsert(event.getCustomer(), event.getPolicy());
  }

  @EventListener
  public void on(DeletePolicyEvent event) {
    store.deletePolicy(event.getPolicyId());
  }
}
