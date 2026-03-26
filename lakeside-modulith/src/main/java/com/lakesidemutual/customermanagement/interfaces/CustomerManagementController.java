package com.lakesidemutual.customermanagement.interfaces;

import com.lakesidemutual.customermanagement.infrastructure.CustomerCoreRemoteProxy;
import com.lakesidemutual.customermanagement.interfaces.dtos.CustomerDto;
import com.lakesidemutual.customermanagement.interfaces.dtos.PaginatedCustomerResponseDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/customers")
class CustomerManagementController {

    private final CustomerCoreRemoteProxy customerCore;

    CustomerManagementController(CustomerCoreRemoteProxy customerCore) {
        this.customerCore = customerCore;
    }

    @GetMapping
    PaginatedCustomerResponseDto getCustomers(
            @RequestParam(required = false) String filter,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        PaginatedCustomerResponseDto dto = customerCore.getCustomers(filter, limit, offset);

        // 1) Add self link for the collection
        dto.add(linkTo(methodOn(CustomerManagementController.class)
                .getCustomers(filter, limit, offset))
                .withSelfRel());

        // 2) Add next/prev links (offset-based pagination)
        int size = dto.getSize(); // total number of customers matching filter
        int nextOffset = offset + limit;
        int prevOffset = Math.max(offset - limit, 0);

        if (nextOffset < size) {
            dto.add(linkTo(methodOn(CustomerManagementController.class)
                    .getCustomers(filter, limit, nextOffset))
                    .withRel("next"));
        }
        if (offset > 0) {
            dto.add(linkTo(methodOn(CustomerManagementController.class)
                    .getCustomers(filter, limit, prevOffset))
                    .withRel("prev"));
        }

        // 3) Add self link for each customer
        if (dto.getCustomers() != null) {
            for (CustomerDto c : dto.getCustomers()) {
                c.add(linkTo(methodOn(CustomerManagementController.class)
                        .getCustomer(c.getCustomerId()))
                        .withSelfRel());
            }
        }

        return dto;
    }

    @GetMapping("/{customerId}")
    CustomerDto getCustomer(@PathVariable String customerId) {

        CustomerDto dto = customerCore.getCustomer(
            new com.lakesidemutual.customermanagement.domain.customer.CustomerId(customerId)
        );

        // Build a URL based on THIS request: /customers/{id}  -> /customers/{id}/address
        String changeAddressHref = ServletUriComponentsBuilder
            .fromCurrentRequestUri()
            .path("/address")
            .toUriString();

        dto.add(Link.of(changeAddressHref).withRel("address.change"));

        return dto;
    }
}
