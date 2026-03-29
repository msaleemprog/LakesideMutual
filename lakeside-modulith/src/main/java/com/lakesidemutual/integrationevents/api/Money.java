package com.lakesidemutual.integrationevents.api;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount,
        String currencyCode
) {}