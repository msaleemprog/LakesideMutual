package com.lakesidemutual.customerselfservice.infrastructure;

import com.lakesidemutual.customerselfservice.domain.customer.CustomerId;
import com.lakesidemutual.customerselfservice.interfaces.dtos.city.CitiesResponseDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.AddressDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.CustomerDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.CustomerProfileDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.CustomerProfileUpdateRequestDto;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("customerselfservice")
@Component
public class InProcessCustomerCoreClient implements CustomerCoreClient {

    private final com.lakesidemutual.customercore.api.CustomerCoreApi customerCoreApi;

    public InProcessCustomerCoreClient(com.lakesidemutual.customercore.api.CustomerCoreApi customerCoreApi) {
        this.customerCoreApi = customerCoreApi;
    }

    @Override
    public CustomerDto getCustomer(CustomerId customerId) {
        // API uses String id now
        com.lakesidemutual.customercore.api.dto.CustomersResponseDto body =
                customerCoreApi.getCustomer(customerId.getId(), "");

        if (body == null || body.getCustomers() == null || body.getCustomers().isEmpty()) {
            return null;
        }

        com.lakesidemutual.customercore.api.dto.CustomerResponseDto coreCustomer =
                body.getCustomers().get(0);

        return mapCoreCustomerToSelfService(coreCustomer);
    }

    @Override
    public ResponseEntity<AddressDto> changeAddress(CustomerId customerId, AddressDto requestDto) {
        com.lakesidemutual.customercore.api.dto.AddressDto coreAddress =
                mapSelfServiceAddressToCore(requestDto);

        ResponseEntity<com.lakesidemutual.customercore.api.dto.AddressDto> coreResponse =
                customerCoreApi.changeAddress(customerId.getId(), coreAddress);

        com.lakesidemutual.customercore.api.dto.AddressDto body = coreResponse.getBody();
        AddressDto mapped = (body != null) ? mapCoreAddressToSelfService(body) : null;

        return ResponseEntity.status(coreResponse.getStatusCode()).body(mapped);
    }

    @Override
    public CustomerDto createCustomer(CustomerProfileUpdateRequestDto requestDto) {
        com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto coreReq =
                mapSelfServiceProfileUpdateToCore(requestDto);

        ResponseEntity<com.lakesidemutual.customercore.api.dto.CustomerResponseDto> coreResponse =
                customerCoreApi.createCustomer(coreReq);

        com.lakesidemutual.customercore.api.dto.CustomerResponseDto body = coreResponse.getBody();
        if (body == null) return null;

        return mapCoreCustomerToSelfService(body);
    }

    @Override
    public ResponseEntity<CitiesResponseDto> getCitiesForPostalCode(String postalCode) {
        ResponseEntity<com.lakesidemutual.customercore.api.dto.CitiesResponseDto> coreResponse =
                customerCoreApi.getCitiesForPostalCode(postalCode);

        com.lakesidemutual.customercore.api.dto.CitiesResponseDto body = coreResponse.getBody();

        CitiesResponseDto mapped = new CitiesResponseDto();
        mapped.setCities(body != null ? body.getCities() : List.of());

        return ResponseEntity.status(coreResponse.getStatusCode()).body(mapped);
    }

    // ----------------- mapping helpers -----------------

    private CustomerDto mapCoreCustomerToSelfService(
            com.lakesidemutual.customercore.api.dto.CustomerResponseDto core) {

        CustomerDto dto = new CustomerDto();
        dto.setCustomerId(core.getCustomerId());

        CustomerProfileDto profile = new CustomerProfileDto();
        profile.setFirstname(core.getFirstname());
        profile.setLastname(core.getLastname());
        profile.setEmail(core.getEmail());
        profile.setPhoneNumber(core.getPhoneNumber());

        AddressDto address = new AddressDto();
        address.setStreetAddress(core.getStreetAddress());
        address.setPostalCode(core.getPostalCode());
        address.setCity(core.getCity());
        profile.setCurrentAddress(address);

        dto.setCustomerProfile(profile);
        return dto;
    }

    private com.lakesidemutual.customercore.api.dto.AddressDto mapSelfServiceAddressToCore(AddressDto dto) {
        com.lakesidemutual.customercore.api.dto.AddressDto core =
                new com.lakesidemutual.customercore.api.dto.AddressDto();
        core.setStreetAddress(dto.getStreetAddress());
        core.setPostalCode(dto.getPostalCode());
        core.setCity(dto.getCity());
        return core;
    }

    private AddressDto mapCoreAddressToSelfService(com.lakesidemutual.customercore.api.dto.AddressDto core) {
        AddressDto dto = new AddressDto();
        dto.setStreetAddress(core.getStreetAddress());
        dto.setPostalCode(core.getPostalCode());
        dto.setCity(core.getCity());
        return dto;
    }

    private com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto
    mapSelfServiceProfileUpdateToCore(CustomerProfileUpdateRequestDto dto) {

        com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto core =
                new com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto();

        core.setFirstname(dto.getFirstname());
        core.setLastname(dto.getLastname());
        core.setEmail(dto.getEmail());
        core.setPhoneNumber(dto.getPhoneNumber());
        core.setStreetAddress(dto.getStreetAddress());
        core.setPostalCode(dto.getPostalCode());
        core.setCity(dto.getCity());
        core.setBirthday(dto.getBirthday());

        return core;
    }
}
