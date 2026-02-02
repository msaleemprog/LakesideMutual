package com.lakesidemutual.customercore.api;

public record CustomerSummary(
    String customerId,
    String firstname,
    String lastname,
    String email,
    String phoneNumber,
    String streetAddress,
    String postalCode,
    String city
) {}
