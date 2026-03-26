package com.lakesidemutual.policymanagement.api;

import java.util.Date;

public record CustomerDecisionSubmitted(
        Long requestId,
        Date decidedAt,
        boolean accepted
) {}
