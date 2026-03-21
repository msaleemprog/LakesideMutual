package com.lakesidemutual.policymanagement.api;

import java.util.List;

import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

public interface PolicyRiskProjectionQuery {
  List<PolicyDto> findAllPoliciesForRiskProjection();
}