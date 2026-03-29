package com.lakesidemutual.integrationevents.api;

import java.util.Date;

public record PolicyCreated(
        Long requestId,
        String policyId,
        Date createdAt
) {}