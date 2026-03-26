package com.lakesidemutual.riskmanagement.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

@Component
public class RiskProjectionStore {

  private final Map<String, PolicyDto> policies = new ConcurrentHashMap<>();

  public void upsert(PolicyDto policy) {
    if (policy != null) {
      policies.put(policy.getPolicyId(), policy);
    }
  }

  public void deletePolicy(String policyId) {
    policies.remove(policyId);
  }

  public Map<String, PolicyDto> getPolicies() {
    return policies;
  }
}