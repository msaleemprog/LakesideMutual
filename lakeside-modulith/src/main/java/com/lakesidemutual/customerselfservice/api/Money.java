package com.lakesidemutual.customerselfservice.api;

import java.math.BigDecimal;

public record Money(
        BigDecimal amount,
        String currencyCode
) {}
