package com.lakesidemutual.customercore.api;

@org.springframework.modulith.NamedInterface("api")
public interface CustomerCoreApi {

    com.lakesidemutual.customercore.api.dto.CustomersResponseDto getCustomer(String customerId, String fields);

    org.springframework.http.ResponseEntity<com.lakesidemutual.customercore.api.dto.CustomerResponseDto>
    createCustomer(com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto requestDto);

    org.springframework.http.ResponseEntity<com.lakesidemutual.customercore.api.dto.AddressDto>
    changeAddress(String customerId, com.lakesidemutual.customercore.api.dto.AddressDto requestDto);

    org.springframework.http.ResponseEntity<com.lakesidemutual.customercore.api.dto.CitiesResponseDto>
    getCitiesForPostalCode(String postalCode);
}
