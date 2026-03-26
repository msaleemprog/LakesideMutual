package com.lakesidemutual.policymanagement.api;

import java.util.Date;

public record InsuranceQuoteResponded(
        Long requestId,
        Date respondedAt,
        boolean requestAccepted,
        Date expirationDate,
        Money insurancePremium,
        Money policyLimit
) {}
