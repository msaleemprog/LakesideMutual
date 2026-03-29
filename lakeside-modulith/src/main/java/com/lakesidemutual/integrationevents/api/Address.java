package com.lakesidemutual.integrationevents.api;

public record Address(
        String streetAddress,
        String postalCode,
        String city
) {}