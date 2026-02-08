package com.lakesidemutual.customercore.api;

import com.lakesidemutual.customercore.api.dto.AddressDto;
import com.lakesidemutual.customercore.api.dto.CitiesResponseDto;
import com.lakesidemutual.customercore.api.dto.CustomerProfileUpdateRequestDto;
import com.lakesidemutual.customercore.api.dto.CustomerResponseDto;
import com.lakesidemutual.customercore.api.dto.CustomersResponseDto;
import com.lakesidemutual.customercore.api.dto.PaginatedCustomerResponseDto;
import com.lakesidemutual.customercore.application.CustomerService;
import com.lakesidemutual.customercore.application.Page;
import com.lakesidemutual.customercore.domain.city.CityLookupService;
import com.lakesidemutual.customercore.domain.customer.Address;
import com.lakesidemutual.customercore.domain.customer.CustomerAggregateRoot;
import com.lakesidemutual.customercore.domain.customer.CustomerId;
import com.lakesidemutual.customercore.domain.customer.CustomerProfileEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
class CustomerCoreApiImpl implements CustomerCoreApi {

    private final CustomerService customerService;
    private final CityLookupService cityLookupService;

    CustomerCoreApiImpl(CustomerService customerService, CityLookupService cityLookupService) {
        this.customerService = customerService;
        this.cityLookupService = cityLookupService;
    }

    @Override
    public CustomersResponseDto getCustomer(String customerId, String fields) {
        // CustomerService expects comma-separated IDs as String (same as REST controller)
        List<CustomerAggregateRoot> customers = customerService.getCustomers(customerId);

        Set<String> includedFields = parseIncludedFields(fields);

        List<CustomerResponseDto> dtos = customers.stream()
                .map(c -> new CustomerResponseDto(includedFields, c))
                .toList();

        return new CustomersResponseDto(dtos);
    }

    @Override
    public ResponseEntity<PaginatedCustomerResponseDto> getCustomers(
            String filter,
            Integer limit,
            Integer offset,
            String fields
    ) {
        String safeFilter = (filter == null) ? "" : filter;
        int safeLimit = (limit == null) ? 10 : limit;
        int safeOffset = (offset == null) ? 0 : offset;

        Set<String> includedFields = parseIncludedFields(fields);

        // Assumes CustomerService has paging method:
        // Page<CustomerAggregateRoot> getCustomers(String filter, int limit, int offset)
        Page<CustomerAggregateRoot> page = customerService.getCustomers(safeFilter, safeLimit, safeOffset);

        List<CustomerResponseDto> customers = page.getElements().stream()
                .map(c -> new CustomerResponseDto(includedFields, c))
                .toList();

        PaginatedCustomerResponseDto dto = new PaginatedCustomerResponseDto(
            safeFilter,
            safeLimit,
            safeOffset,
            page.getSize(),
            customers
        );

        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            String customerId,
            CustomerProfileUpdateRequestDto requestDto
    ) {
        CustomerId id = new CustomerId(customerId);
        CustomerProfileEntity updatedProfile = requestDto.toDomainObject();

        var updatedOpt = customerService.updateCustomerProfile(id, updatedProfile);

        if (updatedOpt == null || updatedOpt.isEmpty()) {
            throw new java.util.NoSuchElementException(
                    "Failed to find a customer with id '" + customerId + "'."
            );
        }

        CustomerResponseDto response = new CustomerResponseDto(Set.of(), updatedOpt.get());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CustomerResponseDto> createCustomer(CustomerProfileUpdateRequestDto requestDto) {
        CustomerProfileEntity profile = requestDto.toDomainObject();
        CustomerAggregateRoot created = customerService.createCustomer(profile);

        CustomerResponseDto response = new CustomerResponseDto(Set.of(), created);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<AddressDto> changeAddress(String customerId, AddressDto requestDto) {
        Address updatedAddress = requestDto.toDomainObject();

        CustomerId coreId = new CustomerId(customerId);
        var updatedOpt = customerService.updateAddress(coreId, updatedAddress);

        if (updatedOpt == null || updatedOpt.isEmpty()) {
            throw new java.util.NoSuchElementException(
                    "Failed to find a customer with id '" + customerId + "'."
            );
        }

        CustomerAggregateRoot customer = updatedOpt.get();
        AddressDto response = AddressDto.fromDomainObject(customer.getCustomerProfile().getCurrentAddress());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CitiesResponseDto> getCitiesForPostalCode(String postalCode) {
        List<String> cities = cityLookupService.getCitiesForPostalCode(postalCode);
        return ResponseEntity.ok(new CitiesResponseDto(cities));
    }

    private Set<String> parseIncludedFields(String fields) {
        if (fields == null || fields.trim().isEmpty()) {
            return Set.of();
        }
        return Set.of(fields.split(","));
    }
}
