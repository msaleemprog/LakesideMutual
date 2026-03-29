package com.lakesidemutual.customerselfservice;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(
        displayName = "Customer Self Service",
        allowedDependencies = { "customercore::api", "integrationevents::api" }
)
public class CustomerSelfServiceModule { }