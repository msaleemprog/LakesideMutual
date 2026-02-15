package com.lakesidemutual.riskmanagement.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lakesidemutual.policymanagement.domain.policy.PolicyAggregateRoot;
import com.lakesidemutual.policymanagement.infrastructure.PolicyRepository;
import com.lakesidemutual.policymanagement.interfaces.dtos.customer.CustomerDto;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

@Service
public class RiskProjectionSyncService {

  private final PolicyRepository policyRepository;
  private final RiskProjectionStore store;

  public RiskProjectionSyncService(PolicyRepository policyRepository, RiskProjectionStore store) {
    this.policyRepository = policyRepository;
    this.store = store;
  }

  @Transactional(readOnly = true)
  public void rebuildFromDatabase() {
    store.getPolicies().clear();
    store.getCustomers().clear();

    List<PolicyAggregateRoot> policies = policyRepository.findAllWithAgreementItems();

    for (PolicyAggregateRoot p : policies) {
      // within transaction → Hibernate session is open → lazy collections can load
      PolicyDto policyDto = PolicyDto.fromDomainObject(p);

      CustomerDto customerDto = new CustomerDto();
      customerDto.setCustomerId(p.getCustomerId().getId());

      store.upsert(customerDto, policyDto);
    }
  }
}
