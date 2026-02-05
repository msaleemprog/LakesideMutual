package com.lakesidemutual.customerselfservice.api;

import java.util.Date;

/**
 * Published by PolicyManagement after creating a policy.
 * Consumed by CustomerSelfService to transition -> POLICY_CREATED.
 */
public record PolicyCreated(
        Long requestId,
        Date createdAt,
        String policyId
) {}
