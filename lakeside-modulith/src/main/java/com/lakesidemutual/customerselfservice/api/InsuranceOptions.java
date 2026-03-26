package com.lakesidemutual.customerselfservice.api;

import java.util.Date;

public record InsuranceOptions(
        Date startDate,
        String insuranceType,
        Money deductible
) {}
