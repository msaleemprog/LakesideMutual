package com.lakesidemutual.customercore.api;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lakesidemutual.customercore.interfaces.CustomerInformationHolder;
import com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerResponseDto;
import com.lakesidemutual.customercore.interfaces.dtos.customer.CustomersResponseDto;

@Service
class CustomerCoreApiImpl implements CustomerCoreApi {

  private final CustomerInformationHolder controller;

  CustomerCoreApiImpl(CustomerInformationHolder controller) {
    this.controller = controller;
  }

  @Override
  public CustomerSummary getCustomerSummary(String customerId) {
    CustomersResponseDto dto = controller.getCustomer(customerId, "").getBody();
    List<CustomerResponseDto> customers = dto != null ? dto.getCustomers() : List.of();
    if (customers.isEmpty()) return null;

    CustomerResponseDto c = customers.get(0);
    return new CustomerSummary(
        c.getCustomerId(),
        c.getFirstname(),
        c.getLastname(),
        c.getEmail(),
        c.getPhoneNumber(),
        c.getStreetAddress(),
        c.getPostalCode(),
        c.getCity()
    );
  }
}
