package com.lakesidemutual.policymanagement.api;

public class PolicyRiskProjectionDeleted {

    private final String policyId;

    public PolicyRiskProjectionDeleted(String policyId) {
        this.policyId = policyId;
    }

    public String getPolicyId() {
        return policyId;
    }
}