package com.lakesidemutual.customerselfservice.api;

public record Address(
        String streetAddress,
        String postalCode,
        String city
) {}
