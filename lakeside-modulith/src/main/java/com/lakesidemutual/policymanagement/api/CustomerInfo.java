package com.lakesidemutual.policymanagement.api;

public record CustomerInfo(
        String customerId,
        String firstname,
        String lastname,
        Address contactAddress,
        Address billingAddress
) {}
