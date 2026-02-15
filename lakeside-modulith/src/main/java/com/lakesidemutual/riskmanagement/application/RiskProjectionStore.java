package com.lakesidemutual.riskmanagement.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.lakesidemutual.policymanagement.interfaces.dtos.customer.CustomerDto;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

@Component
public class RiskProjectionStore {

  // policyId -> policy snapshot
  private final Map<String, PolicyDto> policies = new ConcurrentHashMap<>();

  // customerId -> customer snapshot
  private final Map<String, CustomerDto> customers = new ConcurrentHashMap<>();

  public void upsert(CustomerDto customer, PolicyDto policy) {
    if (customer != null) customers.put(customer.getCustomerId(), customer);
    if (policy != null) policies.put(policy.getPolicyId(), policy);
  }

  public void deletePolicy(String policyId) {
    policies.remove(policyId);
  }

  public Map<String, PolicyDto> getPolicies() { return policies; }
  public Map<String, CustomerDto> getCustomers() { return customers; }
}
