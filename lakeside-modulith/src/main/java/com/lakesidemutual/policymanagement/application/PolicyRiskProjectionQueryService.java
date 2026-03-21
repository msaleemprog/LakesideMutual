package com.lakesidemutual.policymanagement.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakesidemutual.policymanagement.api.PolicyRiskProjectionQuery;
import com.lakesidemutual.policymanagement.infrastructure.PolicyRepository;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

@Service
class PolicyRiskProjectionQueryService implements PolicyRiskProjectionQuery {

  private final PolicyRepository policyRepository;

  PolicyRiskProjectionQueryService(PolicyRepository policyRepository) {
    this.policyRepository = policyRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public List<PolicyDto> findAllPoliciesForRiskProjection() {
    return policyRepository.findAllWithAgreementItems()
        .stream()
        .map(PolicyDto::fromDomainObject)
        .toList();
  }
}