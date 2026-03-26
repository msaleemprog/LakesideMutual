package com.lakesidemutual.customermanagement.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lakesidemutual.customercore.api.CustomerCoreApi;
import com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto;
import com.lakesidemutual.customermanagement.domain.customer.CustomerId;
import com.lakesidemutual.customermanagement.interfaces.dtos.CustomerCoreNotAvailableException;
import com.lakesidemutual.customermanagement.interfaces.dtos.CustomerDto;
import com.lakesidemutual.customermanagement.interfaces.dtos.CustomerProfileDto;
import com.lakesidemutual.customermanagement.interfaces.dtos.PaginatedCustomerResponseDto;
import org.microserviceapipatterns.domaindrivendesign.InfrastructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerCoreRemoteProxy implements InfrastructureService {

  private static final Logger logger = LoggerFactory.getLogger(CustomerCoreRemoteProxy.class);

  private final CustomerCoreApi customerCoreApi;
  private final ObjectMapper objectMapper;

  public CustomerCoreRemoteProxy(CustomerCoreApi customerCoreApi, ObjectMapper objectMapper) {
    this.customerCoreApi = customerCoreApi;
    this.objectMapper = objectMapper;
  }

  public CustomerDto getCustomer(CustomerId customerId) {
    try {
      var customersResponse = customerCoreApi.getCustomer(customerId.getId(), "");
	  if (customersResponse.getCustomers() == null || customersResponse.getCustomers().isEmpty()) {
    	return null;
	  }
	  var ccCustomer = customersResponse.getCustomers().get(0);
        return objectMapper.convertValue(ccCustomer, CustomerDto.class);

    } catch (Exception e) {
      final String errorMessage = "Failed to access Customer Core (in-process).";
      logger.info(errorMessage, e);
      throw new CustomerCoreNotAvailableException(errorMessage);
    }
  }

  public PaginatedCustomerResponseDto getCustomers(String filter, int limit, int offset) {
    try {
      var ccResponseEntity = customerCoreApi.getCustomers(filter, limit, offset, "");
      var ccBody = ccResponseEntity.getBody();
      return objectMapper.convertValue(ccBody, PaginatedCustomerResponseDto.class);

    } catch (Exception e) {
      final String errorMessage = "Failed to access Customer Core (in-process).";
      logger.info(errorMessage, e);
      throw new CustomerCoreNotAvailableException(errorMessage);
    }
  }

  public ResponseEntity<CustomerDto> updateCustomer(CustomerId customerId, CustomerProfileDto customerProfile) {
    try {
      // Convert CM DTO -> Customer Core API DTO
      CustomerProfileUpdateRequestDto ccRequest =
          objectMapper.convertValue(customerProfile, CustomerProfileUpdateRequestDto.class);

      var ccResponseEntity = customerCoreApi.updateCustomer(customerId.getId(), ccRequest);
      CustomerDto cmCustomer = objectMapper.convertValue(ccResponseEntity.getBody(), CustomerDto.class);

      return ResponseEntity.ok(cmCustomer);

    } catch (Exception e) {
      final String errorMessage = "Failed to access Customer Core (in-process).";
      logger.info(errorMessage, e);
      throw new CustomerCoreNotAvailableException(errorMessage);
    }
  }
}
