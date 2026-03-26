package com.lakesidemutual.customerselfservice.api;

import java.util.Date;

/**
 * Published by CustomerSelfService when a quote request is submitted.
 * Consumed by PolicyManagement.
 *
 * Part of the CustomerSelfService module public API (named interface "api").
 */
public record InsuranceQuoteRequestSubmitted(
        Long requestId,
        Date submittedAt,
        CustomerInfo customerInfo,
        InsuranceOptions insuranceOptions
) {}
