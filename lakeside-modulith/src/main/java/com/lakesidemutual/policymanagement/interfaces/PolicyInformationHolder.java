package com.lakesidemutual.policymanagement.interfaces;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.lakesidemutual.policymanagement.api.PolicyRiskProjectionDeleted;
import com.lakesidemutual.policymanagement.api.PolicyRiskProjectionUpdated;
import com.lakesidemutual.policymanagement.domain.customer.CustomerId;
import com.lakesidemutual.policymanagement.domain.policy.InsuringAgreementEntity;
import com.lakesidemutual.policymanagement.domain.policy.MoneyAmount;
import com.lakesidemutual.policymanagement.domain.policy.PolicyAggregateRoot;
import com.lakesidemutual.policymanagement.domain.policy.PolicyId;
import com.lakesidemutual.policymanagement.domain.policy.PolicyPeriod;
import com.lakesidemutual.policymanagement.domain.policy.PolicyType;
import com.lakesidemutual.policymanagement.infrastructure.PolicyRepository;
import com.lakesidemutual.policymanagement.infrastructure.RiskManagementEventPublisher;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.CreatePolicyRequestDto;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PaginatedPolicyResponseDto;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyDto;
import com.lakesidemutual.policymanagement.interfaces.dtos.policy.PolicyNotFoundException;
import com.lakesidemutual.policymanagement.interfaces.dtos.customer.CustomerDto;
@RestController
@RequestMapping("/policies")
public class PolicyInformationHolder {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private PolicyRepository policyRepository;

  @Autowired
  private RiskManagementEventPublisher riskManagementEventPublisher;

  @Operation(summary = "Create a new policy.")
  @PostMapping
  public ResponseEntity<PolicyDto> createPolicy(
      @Parameter(description = "the policy that is to be added", required = true)
      @Valid
      @RequestBody
      CreatePolicyRequestDto createPolicyDto,
      HttpServletRequest request) {

    String customerIdString = createPolicyDto.getCustomerId();
    logger.info("Creating a new policy for customer with id '{}'", customerIdString);
    CustomerId customerId = new CustomerId(customerIdString);

    PolicyId id = PolicyId.random();
    PolicyType policyType = new PolicyType(createPolicyDto.getPolicyType());
    PolicyPeriod policyPeriod = createPolicyDto.getPolicyPeriod().toDomainObject();
    MoneyAmount deductible = createPolicyDto.getDeductible().toDomainObject();
    MoneyAmount policyLimit = createPolicyDto.getPolicyLimit().toDomainObject();
    MoneyAmount insurancePremium = createPolicyDto.getInsurancePremium().toDomainObject();
    InsuringAgreementEntity insuringAgreement = createPolicyDto.getInsuringAgreement().toDomainObject();

    PolicyAggregateRoot policy = new PolicyAggregateRoot(
        id, customerId, new Date(), policyPeriod, policyType,
        deductible, policyLimit, insurancePremium, insuringAgreement);

    policyRepository.save(policy);

    PolicyDto policyDto = createPolicyDtos(Arrays.asList(policy), "").get(0);
    // helpful link
    policyDto.add(linkTo(methodOn(PolicyInformationHolder.class).getPolicy(id, "")).withSelfRel());
    policyDto.add(org.springframework.hateoas.Link.of("/customers/" + policyDto.getCustomerId()).withRel("customer"));

    // Publish UPDATE event for Risk Management projection (CREATE is treated as an upsert)
    riskManagementEventPublisher.publish(new PolicyRiskProjectionUpdated(policyDto));
    return ResponseEntity.ok(policyDto);
  }

  @Operation(summary = "Update an existing policy.")
  @PutMapping(value = "/{policyId}")
  public ResponseEntity<PolicyDto> updatePolicy(
      @Parameter(description = "the policy's unique id", required = true) @PathVariable PolicyId policyId,
      @Parameter(description = "the updated policy", required = true) @Valid @RequestBody CreatePolicyRequestDto createPolicyDto,
      HttpServletRequest request) {

    logger.info("Updating policy with id '{}'", policyId.getId());

    Optional<PolicyAggregateRoot> optPolicy = policyRepository.findById(policyId);
    if (!optPolicy.isPresent()) {
      final String errorMessage = "Failed to find a policy with id '{}'";
      logger.warn(errorMessage, policyId.getId());
      throw new PolicyNotFoundException(errorMessage);
    }

    PolicyType policyType = new PolicyType(createPolicyDto.getPolicyType());
    PolicyPeriod policyPeriod = createPolicyDto.getPolicyPeriod().toDomainObject();
    MoneyAmount deductible = createPolicyDto.getDeductible().toDomainObject();
    MoneyAmount policyLimit = createPolicyDto.getPolicyLimit().toDomainObject();
    MoneyAmount insurancePremium = createPolicyDto.getInsurancePremium().toDomainObject();
    InsuringAgreementEntity insuringAgreement = createPolicyDto.getInsuringAgreement().toDomainObject();

    PolicyAggregateRoot policy = optPolicy.get();
    policy.setPolicyPeriod(policyPeriod);
    policy.setPolicyType(policyType);
    policy.setDeductible(deductible);
    policy.setPolicyLimit(policyLimit);
    policy.setInsurancePremium(insurancePremium);
    policy.setInsuringAgreement(insuringAgreement);

    policyRepository.save(policy);

    PolicyDto response = createPolicyDtos(Arrays.asList(policy), "").get(0);
    response.add(linkTo(methodOn(PolicyInformationHolder.class).getPolicy(policyId, "")).withSelfRel());
    response.add(org.springframework.hateoas.Link.of("/customers/" + response.getCustomerId()).withRel("customer"));

    // Publish UPDATE event for Risk Management projection
    riskManagementEventPublisher.publish(new PolicyRiskProjectionUpdated(response));

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Delete an existing policy.")
  @DeleteMapping(value = "/{policyId}")
  public ResponseEntity<Void> deletePolicy(
      @Parameter(description = "the policy's unique id", required = true) @PathVariable PolicyId policyId,
      HttpServletRequest request) {

    logger.info("Deleting policy with id '{}'", policyId.getId());
    policyRepository.deleteById(policyId);

    riskManagementEventPublisher.publish(new PolicyRiskProjectionDeleted(policyId.getId()));

    return ResponseEntity.noContent().build();
  }

  /**
   * Option A expansion:
   * - default: PolicyDto.customer is a String (customerId)
   * - expand=customer: PolicyDto.customer becomes a CustomerDto (object) with at least customerId set
   */
  private List<PolicyDto> createPolicyDtos(List<PolicyAggregateRoot> policies, String expand) {

    List<PolicyDto> policyDtos = new ArrayList<>();

    boolean expandCustomer = "customer".equals(expand);

    for (PolicyAggregateRoot policy : policies) {

      PolicyDto policyDto = PolicyDto.fromDomainObject(policy);

      // Add stable customer link (use customerId extracted from PolicyDto)
      policyDto.add(org.springframework.hateoas.Link.of("/customers/" + policyDto.getCustomerId()).withRel("customer"));

      // Minimal expansion to satisfy frontend route building:
      // policy.customer.customerId must exist when expand=customer
      if (expandCustomer) {
        CustomerDto minimal = new CustomerDto();
        minimal.setCustomerId(policy.getCustomerId().getId());
        policyDto.setCustomer(minimal);
      }

      policyDtos.add(policyDto);
    }

    return policyDtos;
  }

  private PaginatedPolicyResponseDto createPaginatedPolicyResponseDto(
      Integer limit, Integer offset, String expand, int size, List<PolicyDto> policyDtos) {

    PaginatedPolicyResponseDto paginatedPolicyResponseDto =
        new PaginatedPolicyResponseDto(limit, offset, size, policyDtos);

    paginatedPolicyResponseDto.add(
        linkTo(methodOn(PolicyInformationHolder.class).getPolicies(limit, offset, expand)).withSelfRel());

    if (offset > 0) {
      paginatedPolicyResponseDto.add(linkTo(
          methodOn(PolicyInformationHolder.class).getPolicies(limit, Math.max(0, offset - limit), expand))
          .withRel("prev"));
    }

    if (offset < size - limit) {
      paginatedPolicyResponseDto.add(
          linkTo(methodOn(PolicyInformationHolder.class).getPolicies(limit, offset + limit, expand))
              .withRel("next"));
    }

    return paginatedPolicyResponseDto;
  }

  @Operation(summary = "Get all policies, newest first.")
  @GetMapping
  public ResponseEntity<PaginatedPolicyResponseDto> getPolicies(
      @Parameter(description = "the maximum number of policies per page", required = false)
      @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
      @Parameter(description = "the offset of the page's first policy", required = false)
      @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
      @Parameter(description = "a comma-separated list of the fields that should be expanded in the response", required = false)
      @RequestParam(value = "expand", required = false, defaultValue = "") String expand) {

    logger.debug("Fetching a page of policies (offset={},limit={},fields='{}')", offset, limit, expand);

    List<PolicyAggregateRoot> allPolicies = policyRepository.findAll(
        Sort.by(Sort.Direction.DESC, PolicyAggregateRoot.FIELD_CREATION_DATE));

    List<PolicyAggregateRoot> policies = allPolicies.stream().skip(offset).limit(limit).collect(Collectors.toList());

    List<PolicyDto> policyDtos = createPolicyDtos(policies, expand);

    PaginatedPolicyResponseDto paginatedPolicyResponse =
        createPaginatedPolicyResponseDto(limit, offset, expand, allPolicies.size(), policyDtos);

    return ResponseEntity.ok(paginatedPolicyResponse);
  }

  @Operation(summary = "Get a single policy.")
  @GetMapping(value = "/{policyId}")
  public ResponseEntity<PolicyDto> getPolicy(
      @Parameter(description = "the policy's unique id", required = true) @PathVariable PolicyId policyId,
      @Parameter(description = "a comma-separated list of the fields that should be expanded in the response", required = false)
      @RequestParam(value = "expand", required = false, defaultValue = "") String expand) {

    logger.debug("Fetching policy with id '{}'", policyId.getId());

    Optional<PolicyAggregateRoot> optPolicy = policyRepository.findById(policyId);
    if (!optPolicy.isPresent()) {
      final String errorMessage = "Failed to find a policy with id '{}'";
      logger.warn(errorMessage, policyId.getId());
      throw new PolicyNotFoundException(errorMessage);
    }

    PolicyAggregateRoot policy = optPolicy.get();
    PolicyDto response = createPolicyDtos(Arrays.asList(policy), expand).get(0);

    // also add self link
    response.add(linkTo(methodOn(PolicyInformationHolder.class).getPolicy(policyId, expand)).withSelfRel());

    return ResponseEntity.ok(response);
  }
}
