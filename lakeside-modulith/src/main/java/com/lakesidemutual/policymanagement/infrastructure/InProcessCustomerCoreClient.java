package com.lakesidemutual.policymanagement.infrastructure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.lakesidemutual.customercore.api.CustomerCoreApi;
import com.lakesidemutual.customercore.api.dto.CustomerResponseDto;
import com.lakesidemutual.policymanagement.domain.customer.CustomerId;
import com.lakesidemutual.policymanagement.interfaces.dtos.customer.AddressDto;
import com.lakesidemutual.policymanagement.interfaces.dtos.customer.CustomerDto;
import com.lakesidemutual.policymanagement.interfaces.dtos.customer.CustomerProfileDto;

/**
 * Replaces CustomerCoreRemoteProxy (HTTP). Uses customercore::api in-process.
 */

@Profile("policymanagement")
@Component
public class InProcessCustomerCoreClient {

    private final CustomerCoreApi customerCoreApi;

    public InProcessCustomerCoreClient(CustomerCoreApi customerCoreApi) {
        this.customerCoreApi = customerCoreApi;
    }

    public List<CustomerDto> getCustomersById(CustomerId... customerIds) {
        List<CustomerDto> result = new ArrayList<>();
        for (CustomerId id : customerIds) {
            // fields: empty -> include everything (based on your current customercore API behavior)
            var resp = customerCoreApi.getCustomer(id.getId(), "");
            if (resp != null && resp.getCustomers() != null && !resp.getCustomers().isEmpty()) {
                CustomerResponseDto c = resp.getCustomers().get(0);
                result.add(map(c));
            }
        }
        return result;
    }

    public CustomerDto getCustomer(CustomerId customerId) {
        var list = getCustomersById(customerId);
        return list.isEmpty() ? null : list.get(0);
    }

    private static CustomerDto map(CustomerResponseDto c) {

    // moveHistory -> List<AddressDto>
    List<AddressDto> moveHistory = c.getMoveHistory() == null
            ? List.of()
            : c.getMoveHistory().stream().map(a -> {
                AddressDto ad = new AddressDto();
                ad.setStreetAddress(a.getStreetAddress());
                ad.setPostalCode(a.getPostalCode());
                ad.setCity(a.getCity());
                return ad;
            }).toList();

    // currentAddress: not available directly from CustomerResponseDto in your API
    AddressDto currentAddress = moveHistory.isEmpty() ? null : moveHistory.get(moveHistory.size() - 1);

    CustomerProfileDto profile = new CustomerProfileDto();
    profile.setFirstname(c.getFirstname());
    profile.setLastname(c.getLastname());
    profile.setBirthday(c.getBirthday());
    profile.setEmail(c.getEmail());
    profile.setPhoneNumber(c.getPhoneNumber());
    profile.setCurrentAddress(currentAddress);
    profile.setMoveHistory(moveHistory);

    CustomerDto dto = new CustomerDto();
    dto.setCustomerId(c.getCustomerId());
    dto.setCustomerProfile(profile);
    return dto;
}

}
