package com.lakesidemutual.integrationevents.api;

import java.util.Date;

public record InsuranceQuoteRequestSubmitted(
        Long requestId,
        Date submittedAt,
        CustomerInfo customerInfo,
        InsuranceOptions insuranceOptions
) {}