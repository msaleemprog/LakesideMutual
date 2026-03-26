package com.lakesidemutual.policymanagement.api;

import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

public class PolicyRiskProjectionUpdated {

  private final PolicyDto policy;

  public PolicyRiskProjectionUpdated(PolicyDto policy) {
    this.policy = policy;
  }

  public PolicyDto getPolicy() {
    return policy;
  }
}