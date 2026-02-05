package com.lakesidemutual.customerselfservice.api;

import java.util.Date;

/**
 * Published by PolicyManagement after checking a quote request.
 * Consumed by CustomerSelfService to transition REQUEST_SUBMITTED -> QUOTE_RECEIVED (or REQUEST_REJECTED).
 */
public record InsuranceQuoteResponded(
        Long requestId,
        Date respondedAt,
        boolean requestAccepted,
        Date expirationDate,
        Money insurancePremium,
        Money policyLimit
) {}
