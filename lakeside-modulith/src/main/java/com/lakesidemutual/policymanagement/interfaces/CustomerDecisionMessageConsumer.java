package com.lakesidemutual.policymanagement.interfaces;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lakesidemutual.policymanagement.domain.customer.CustomerId;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.CustomerDecisionEvent;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.InsuranceQuoteRequestAggregateRoot;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.RequestStatus;
import com.lakesidemutual.policymanagement.domain.policy.InsuringAgreementEntity;
import com.lakesidemutual.policymanagement.domain.policy.MoneyAmount;
import com.lakesidemutual.policymanagement.domain.policy.PolicyAggregateRoot;
import com.lakesidemutual.policymanagement.domain.policy.PolicyId;
import com.lakesidemutual.policymanagement.domain.policy.PolicyPeriod;
import com.lakesidemutual.policymanagement.domain.policy.PolicyType;
import com.lakesidemutual.policymanagement.infrastructure.PolicyInsuranceQuoteRequestRepository;
import com.lakesidemutual.policymanagement.infrastructure.PolicyRepository;

/**
 * Transport-agnostic handler. Called by the Modulith event listener.
 * No JMS, no remote calls, no cross-module messaging.
 */
@Component
public class CustomerDecisionMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDecisionMessageConsumer.class);

    private final PolicyInsuranceQuoteRequestRepository insuranceQuoteRequestRepository;
    private final PolicyRepository policyRepository;

    public CustomerDecisionMessageConsumer(
            PolicyInsuranceQuoteRequestRepository insuranceQuoteRequestRepository,
            PolicyRepository policyRepository
    ) {
        this.insuranceQuoteRequestRepository = insuranceQuoteRequestRepository;
        this.policyRepository = policyRepository;
    }

    public void handleCustomerDecisionEvent(final CustomerDecisionEvent customerDecisionEvent) {

        final Long id = customerDecisionEvent.getInsuranceQuoteRequestId();
        final Optional<InsuranceQuoteRequestAggregateRoot> insuranceQuoteRequestOpt =
                insuranceQuoteRequestRepository.findById(id);

        if (insuranceQuoteRequestOpt.isEmpty()) {
            logger.error("Unable to process customer decision: unknown insurance quote request id {}", id);
            return;
        }

        final InsuranceQuoteRequestAggregateRoot insuranceQuoteRequest = insuranceQuoteRequestOpt.get();
        final Date decisionDate = customerDecisionEvent.getDate();

        if (customerDecisionEvent.isQuoteAccepted()) {

            // Keep existing expiration logic, but only persist locally
            if (insuranceQuoteRequest.getStatus().equals(RequestStatus.QUOTE_EXPIRED)
                    || insuranceQuoteRequest.hasQuoteExpired(decisionDate)) {

                Date expirationDate;
                if (insuranceQuoteRequest.getStatus().equals(RequestStatus.QUOTE_EXPIRED)) {
                    expirationDate = insuranceQuoteRequest.popStatus().getDate();
                } else {
                    expirationDate = decisionDate;
                }

                insuranceQuoteRequest.acceptQuote(decisionDate);
                insuranceQuoteRequest.markQuoteAsExpired(expirationDate);

                logger.info("Quote {} accepted after expiration (expired at {}).",
                        insuranceQuoteRequest.getId(), expirationDate);

            } else {
                logger.info("The insurance quote for request {} has been accepted", insuranceQuoteRequest.getId());

                insuranceQuoteRequest.acceptQuote(decisionDate);

                PolicyAggregateRoot policy = createPolicyForInsuranceQuoteRequest(insuranceQuoteRequest);
                policyRepository.save(policy);

                Date policyCreationDate = new Date();
                insuranceQuoteRequest.finalizeQuote(policy.getId().getId(), policyCreationDate);

                logger.info("Policy {} created for quote request {}.",
                        policy.getId().getId(), insuranceQuoteRequest.getId());
            }

        } else {
            if (insuranceQuoteRequest.getStatus().equals(RequestStatus.QUOTE_EXPIRED)) {
                insuranceQuoteRequest.popStatus();
            }

            logger.info("The insurance quote for request {} has been rejected", insuranceQuoteRequest.getId());
            insuranceQuoteRequest.rejectQuote(decisionDate);
        }

        insuranceQuoteRequestRepository.save(insuranceQuoteRequest);
    }

    private PolicyAggregateRoot createPolicyForInsuranceQuoteRequest(InsuranceQuoteRequestAggregateRoot insuranceQuoteRequest) {
        PolicyId policyId = PolicyId.random();
        CustomerId customerId = insuranceQuoteRequest.getCustomerInfo().getCustomerId();

        Date startDate = insuranceQuoteRequest.getInsuranceOptions().getStartDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 1);
        Date endDate = calendar.getTime();
        PolicyPeriod policyPeriod = new PolicyPeriod(startDate, endDate);

        PolicyType policyType = new PolicyType(insuranceQuoteRequest.getInsuranceOptions().getInsuranceType().getName());
        MoneyAmount deductible = insuranceQuoteRequest.getInsuranceOptions().getDeductible();
        MoneyAmount insurancePremium = insuranceQuoteRequest.getInsuranceQuote().getInsurancePremium();
        MoneyAmount policyLimit = insuranceQuoteRequest.getInsuranceQuote().getPolicyLimit();
        InsuringAgreementEntity insuringAgreement = new InsuringAgreementEntity(Collections.emptyList());

        return new PolicyAggregateRoot(
                policyId, customerId, new Date(), policyPeriod, policyType,
                deductible, policyLimit, insurancePremium, insuringAgreement
        );
    }
}
