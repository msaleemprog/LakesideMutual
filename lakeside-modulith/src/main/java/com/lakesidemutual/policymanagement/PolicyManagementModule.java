package com.lakesidemutual.policymanagement;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(
        displayName = "Policy Management",
        allowedDependencies = { "customercore::api", "integrationevents::api" }
)
public class PolicyManagementModule { }