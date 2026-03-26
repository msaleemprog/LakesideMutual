package com.lakesidemutual.policymanagement.api;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount,
        String currencyCode
) {}
