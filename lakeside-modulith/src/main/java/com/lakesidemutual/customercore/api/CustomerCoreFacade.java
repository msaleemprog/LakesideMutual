package com.lakesidemutual.customercore.api;

import org.springframework.stereotype.Component;
import com.lakesidemutual.customercore.interfaces.CustomerInformationHolder;
import com.lakesidemutual.customercore.interfaces.dtos.customer.CustomersResponseDto;

@Component
public class CustomerCoreFacade {

    private final CustomerInformationHolder customerController;

    public CustomerCoreFacade(CustomerInformationHolder customerController) {
        this.customerController = customerController;
    }

    public CustomersResponseDto getCustomer(String customerId) {
        return customerController.getCustomer(customerId, "").getBody();
    }
}
