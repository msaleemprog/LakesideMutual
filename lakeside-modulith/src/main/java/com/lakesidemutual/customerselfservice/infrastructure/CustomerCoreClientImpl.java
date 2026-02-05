package com.lakesidemutual.customerselfservice.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.lakesidemutual.customerselfservice.domain.customer.CustomerId;
import com.lakesidemutual.customerselfservice.interfaces.dtos.city.CitiesResponseDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.AddressDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.CustomerDto;
import com.lakesidemutual.customerselfservice.interfaces.dtos.customer.CustomerProfileUpdateRequestDto;

/**
 * Modulith migration adapter.
 *
 * Option A (in-process) means: this bean exists so wiring succeeds,
 * and later you replace these method bodies to call Customer Core services directly.
 *
 * For now, we return 501/throw to make it explicit what is not implemented yet,
 * while still allowing the Spring context to create the bean.
 */
@Profile("migration-stub")
@Component
public class CustomerCoreClientImpl implements CustomerCoreClient {

    @Override
    public CustomerDto getCustomer(CustomerId customerId) {
        throw new UnsupportedOperationException(
            "CustomerCoreClientImpl.getCustomer(CustomerId) not implemented yet. " +
            "Wire this to Customer Core (in-process) service/facade."
        );
    }

    @Override
    public ResponseEntity<AddressDto> changeAddress(CustomerId customerId, AddressDto requestDto) {
        // returning 501 is safer than throwing if this gets called accidentally
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public CustomerDto createCustomer(CustomerProfileUpdateRequestDto requestDto) {
        throw new UnsupportedOperationException(
            "CustomerCoreClientImpl.createCustomer(CustomerProfileUpdateRequestDto) not implemented yet. " +
            "Wire this to Customer Core (in-process) service/facade."
        );
    }

    @Override
    public ResponseEntity<CitiesResponseDto> getCitiesForPostalCode(String postalCode) {
        // returning 501 is safer than throwing if CityReferenceDataHolder calls this during init
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
