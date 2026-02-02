package com.lakesidemutual.customerselfservice.infrastructure;

import com.lakesidemutual.customerselfservice.domain.customer.CustomerId;
import com.lakesidemutual.customerselfservice.interfaces.dtos.city.CitiesResponseDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.AddressDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.CustomerDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.CustomerProfileUpdateRequestDto;
import org.springframework.http.ResponseEntity;

public interface CustomerCoreClient {
    CustomerDto getCustomer(CustomerId customerId);
    ResponseEntity<AddressDto> changeAddress(CustomerId customerId, AddressDto requestDto);
    CustomerDto createCustomer(CustomerProfileUpdateRequestDto requestDto);
    ResponseEntity<CitiesResponseDto> getCitiesForPostalCode(String postalCode);
}
