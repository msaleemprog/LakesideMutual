package com.lakesidemutual.customerselfservice.infrastructure;

import com.lakesidemutual.customercore.interfaces.CityReferenceDataHolder;
import com.lakesidemutual.customercore.interfaces.CustomerInformationHolder;
import com.lakesidemutual.customercore.interfaces.dtos.customer.CustomersResponseDto;
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

@Profile("!customer-core-http")
@Component
public class InProcessCustomerCoreClient implements CustomerCoreClient {

    private final CustomerInformationHolder customerCoreController;
    private final CityReferenceDataHolder cityCoreController;

    public InProcessCustomerCoreClient(CustomerInformationHolder customerCoreController,
                                       CityReferenceDataHolder cityCoreController) {
        this.customerCoreController = customerCoreController;
        this.cityCoreController = cityCoreController;
    }

    @Override
    public CustomerDto getCustomer(CustomerId customerId) {
        // Customer Core controller returns wrapper with list; we take the first element.
        ResponseEntity<CustomersResponseDto> response =
                customerCoreController.getCustomer(customerId.getId(), "");

        CustomersResponseDto body = response.getBody();
        if (body == null || body.getCustomers() == null || body.getCustomers().isEmpty()) {
            return null;
        }

        com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerResponseDto coreCustomer =
                body.getCustomers().get(0);

        return mapCoreCustomerToSelfService(coreCustomer);
    }

    @Override
    public ResponseEntity<AddressDto> changeAddress(CustomerId customerId, AddressDto requestDto) {
        com.lakesidemutual.customercore.interfaces.dtos.customer.AddressDto coreAddress =
                mapSelfServiceAddressToCore(requestDto);

        ResponseEntity<com.lakesidemutual.customercore.interfaces.dtos.customer.AddressDto> coreResponse =
        customerCoreController.changeAddress(
                new com.lakesidemutual.customercore.domain.customer.CustomerId(customerId.getId()),
                coreAddress
        );

        com.lakesidemutual.customercore.interfaces.dtos.customer.AddressDto body = coreResponse.getBody();

        AddressDto mapped = (body != null) ? mapCoreAddressToSelfService(body) : null;

        return ResponseEntity.status(coreResponse.getStatusCode()).body(mapped);
    }

    @Override
    public CustomerDto createCustomer(CustomerProfileUpdateRequestDto requestDto) {
        com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerProfileUpdateRequestDto coreReq =
                mapSelfServiceProfileUpdateToCore(requestDto);

        ResponseEntity<com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerResponseDto> coreResponse =
                customerCoreController.createCustomer(coreReq);

        com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerResponseDto body = coreResponse.getBody();
        if (body == null) return null;

        return mapCoreCustomerToSelfService(body);
    }

    @Override
    public ResponseEntity<CitiesResponseDto> getCitiesForPostalCode(String postalCode) {
        ResponseEntity<com.lakesidemutual.customercore.interfaces.dtos.city.CitiesResponseDto> coreResponse =
                cityCoreController.getCitiesForPostalCode(postalCode);

        com.lakesidemutual.customercore.interfaces.dtos.city.CitiesResponseDto body = coreResponse.getBody();

        CitiesResponseDto mapped = new CitiesResponseDto();
        mapped.setCities(body != null ? body.getCities() : List.of());

        return ResponseEntity.status(coreResponse.getStatusCode()).body(mapped);
    }

    // ----------------- mapping helpers using existing DTOs -----------------

    private CustomerDto mapCoreCustomerToSelfService(
            com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerResponseDto core) {

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

    private com.lakesidemutual.customercore.interfaces.dtos.customer.AddressDto mapSelfServiceAddressToCore(AddressDto dto) {
        com.lakesidemutual.customercore.interfaces.dtos.customer.AddressDto core =
                new com.lakesidemutual.customercore.interfaces.dtos.customer.AddressDto();
        core.setStreetAddress(dto.getStreetAddress());
        core.setPostalCode(dto.getPostalCode());
        core.setCity(dto.getCity());
        return core;
    }

    private AddressDto mapCoreAddressToSelfService(
            com.lakesidemutual.customercore.interfaces.dtos.customer.AddressDto core) {
        AddressDto dto = new AddressDto();
        dto.setStreetAddress(core.getStreetAddress());
        dto.setPostalCode(core.getPostalCode());
        dto.setCity(core.getCity());
        return dto;
    }

    private com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerProfileUpdateRequestDto
    mapSelfServiceProfileUpdateToCore(CustomerProfileUpdateRequestDto dto) {

        com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerProfileUpdateRequestDto core =
                new com.lakesidemutual.customercore.interfaces.dtos.customer.CustomerProfileUpdateRequestDto();

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
