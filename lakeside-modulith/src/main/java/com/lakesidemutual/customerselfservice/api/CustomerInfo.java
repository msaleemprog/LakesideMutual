package com.lakesidemutual.customerselfservice.api;

public record CustomerInfo(
        String customerId,
        String firstname,
        String lastname,
        Address contactAddress,
        Address billingAddress
) {}
