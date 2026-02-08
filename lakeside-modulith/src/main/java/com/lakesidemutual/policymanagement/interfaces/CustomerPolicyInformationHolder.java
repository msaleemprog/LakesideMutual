package com.lakesidemutual.policymanagement.interfaces;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lakesidemutual.policymanagement.domain.customer.CustomerId;
import com.lakesidemutual.policymanagement.domain.policy.PolicyAggregateRoot;
import com.lakesidemutual.policymanagement.infrastructure.PolicyRepository;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PaginatedPolicyResponseDto;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;

@RestController
@RequestMapping("/customers")
public class CustomerPolicyInformationHolder {

  private final PolicyRepository policyRepository;

  public CustomerPolicyInformationHolder(PolicyRepository policyRepository) {
    this.policyRepository = policyRepository;
  }

  @Operation(summary = "Get policies of a specific customer (upstream compatibility endpoint).")
  @GetMapping("/{customerId}/policies")
  public ResponseEntity<PaginatedPolicyResponseDto> getPoliciesOfCustomer(
      @Parameter(required = true) @PathVariable String customerId,
      @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
      @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
      @RequestParam(value = "expand", required = false, defaultValue = "") String expand
  ) {

    CustomerId cid = new CustomerId(customerId);

    // Already ordered by creationDate desc via repository method
    List<PolicyAggregateRoot> all = policyRepository.findAllByCustomerIdOrderByCreationDateDesc(cid);

    List<PolicyAggregateRoot> page = all.stream()
        .skip(offset)
        .limit(limit)
        .collect(Collectors.toList());

    List<PolicyDto> dtos = page.stream()
        .map(PolicyDto::fromDomainObject)
        .collect(Collectors.toList());

    PaginatedPolicyResponseDto response =
        new PaginatedPolicyResponseDto(limit, offset, all.size(), dtos);

    // HATEOAS paging links (matches your style used elsewhere)
    response.add(linkTo(methodOn(CustomerPolicyInformationHolder.class)
        .getPoliciesOfCustomer(customerId, limit, offset, expand)).withSelfRel());

    if (offset > 0) {
      response.add(linkTo(methodOn(CustomerPolicyInformationHolder.class)
          .getPoliciesOfCustomer(customerId, limit, Math.max(0, offset - limit), expand)).withRel("prev"));
    }
    if (offset < all.size() - limit) {
      response.add(linkTo(methodOn(CustomerPolicyInformationHolder.class)
          .getPoliciesOfCustomer(customerId, limit, offset + limit, expand)).withRel("next"));
    }

    return ResponseEntity.ok(response);
  }
}
