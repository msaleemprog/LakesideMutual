package com.lakesidemutual.customercore.api;
import com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto;
import com.lakesidemutual.customercore.api.dto.CustomerResponseDto;
import com.lakesidemutual.customercore.api.dto.PaginatedCustomerResponseDto;
import org.springframework.http.ResponseEntity;

@org.springframework.modulith.NamedInterface("api")
public interface CustomerCoreApi {

    com.lakesidemutual.customercore.api.dto.CustomersResponseDto getCustomer(String customerId, String fields);

    org.springframework.http.ResponseEntity<com.lakesidemutual.customercore.api.dto.CustomerResponseDto>
    createCustomer(com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto requestDto);

    org.springframework.http.ResponseEntity<com.lakesidemutual.customercore.api.dto.AddressDto>
    changeAddress(String customerId, com.lakesidemutual.customercore.api.dto.AddressDto requestDto);

    org.springframework.http.ResponseEntity<com.lakesidemutual.customercore.api.dto.CitiesResponseDto>
    getCitiesForPostalCode(String postalCode);

    ResponseEntity<PaginatedCustomerResponseDto> getCustomers(
        String filter,
        Integer limit,
        Integer offset,
        String fields
    );

    ResponseEntity<CustomerResponseDto> updateCustomer(
        String customerId,
        CustomerProfileUpdateRequestDto requestDto
    );
}
