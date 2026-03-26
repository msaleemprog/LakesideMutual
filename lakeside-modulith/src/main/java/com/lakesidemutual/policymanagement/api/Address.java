package com.lakesidemutual.policymanagement.api;

public record Address(
        String streetAddress,
        String postalCode,
        String city
) {}
