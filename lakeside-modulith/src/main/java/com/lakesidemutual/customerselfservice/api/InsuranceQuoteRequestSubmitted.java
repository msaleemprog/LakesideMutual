package com.lakesidemutual.customerselfservice.api;

import java.util.Date;

public record InsuranceQuoteRequestSubmitted(
        Long requestId,
        Date submittedAt,
        String customerId
) {}
