package com.lakesidemutual.riskmanagement.application;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

@Service
public class RiskReportCsvService {

  public String generateCsv(RiskProjectionStore store) {

    Map<String, PolicyDto> policies = store.getPolicies();

    long customerCount = policies.values().stream()
        .map(PolicyDto::getCustomerId)
        .filter(id -> id != null && !id.isBlank())
        .distinct()
        .count();

    int policyCount = policies.size();

    StringBuilder sb = new StringBuilder();
    sb.append("Portfolio Summary\n");
    sb.append("Customer Count,").append(customerCount).append("\n");
    sb.append("Policy Count,").append(policyCount).append("\n");
    sb.append("\n");
    sb.append("Note,This is a minimal report. Add averages and distributions next.\n");

    return sb.toString();
  }
}