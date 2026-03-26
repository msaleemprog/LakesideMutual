package com.lakesidemutual.riskmanagement.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakesidemutual.policymanagement.api.PolicyRiskProjectionQuery;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

@Service
public class RiskProjectionSyncService {

  private final PolicyRiskProjectionQuery policyRiskProjectionQuery;
  private final RiskProjectionStore store;

  public RiskProjectionSyncService(PolicyRiskProjectionQuery policyRiskProjectionQuery,
      RiskProjectionStore store) {
    this.policyRiskProjectionQuery = policyRiskProjectionQuery;
    this.store = store;
  }

  @Transactional(readOnly = true)
  public void rebuildFromDatabase() {
    store.getPolicies().clear();

    for (PolicyDto policyDto : policyRiskProjectionQuery.findAllPoliciesForRiskProjection()) {
      store.upsert(policyDto);
    }
  }
}