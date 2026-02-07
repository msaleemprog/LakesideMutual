package com.lakesidemutual.policymanagement.infrastructure;

import java.util.Currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.lakesidemutual.policymanagement.api.CustomerDecisionSubmitted;
import com.lakesidemutual.policymanagement.api.InsuranceQuoteRequestSubmitted;
import com.lakesidemutual.policymanagement.api.CustomerInfo;
import com.lakesidemutual.policymanagement.api.Address;
import com.lakesidemutual.policymanagement.api.InsuranceOptions;
import com.lakesidemutual.policymanagement.api.Money;

import com.lakesidemutual.policymanagement.domain.customer.CustomerId;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.CustomerDecisionEvent;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.CustomerInfoEntity;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.InsuranceOptionsEntity;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.InsuranceQuoteRequestAggregateRoot;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.InsuranceType;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.RequestStatus;
import com.lakesidemutual.policymanagement.domain.policy.MoneyAmount;

import com.lakesidemutual.policymanagement.interfaces.CustomerDecisionMessageConsumer;

@Component
class CustomerSelfServiceEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerSelfServiceEventListener.class);

    private final PolicyInsuranceQuoteRequestRepository pmQuoteRepo;
    private final CustomerDecisionMessageConsumer decisionHandler;

    CustomerSelfServiceEventListener(
            PolicyInsuranceQuoteRequestRepository pmQuoteRepo,
            CustomerDecisionMessageConsumer decisionHandler
    ) {
        this.pmQuoteRepo = pmQuoteRepo;
        this.decisionHandler = decisionHandler;
    }

    @EventListener
    void on(InsuranceQuoteRequestSubmitted event) {

        CustomerInfo ci = event.customerInfo();
        Address contact = ci.contactAddress();
        Address billing = ci.billingAddress();

        CustomerInfoEntity pmCustomerInfo = new CustomerInfoEntity(
                new CustomerId(ci.customerId()),
                ci.firstname(),
                ci.lastname(),
                new com.lakesidemutual.policymanagement.domain.insurancequoterequest.Address(
                        contact.streetAddress(), contact.postalCode(), contact.city()
                ),
                new com.lakesidemutual.policymanagement.domain.insurancequoterequest.Address(
                        billing.streetAddress(), billing.postalCode(), billing.city()
                )
        );

        InsuranceOptions io = event.insuranceOptions();
        Money deductible = io.deductible();

        InsuranceOptionsEntity pmOptions = new InsuranceOptionsEntity(
                io.startDate(),
                new InsuranceType(io.insuranceType()),
                new MoneyAmount(
                        deductible.amount(),
                        Currency.getInstance(deductible.currencyCode())
                )
        );

        InsuranceQuoteRequestAggregateRoot pmAgg = new InsuranceQuoteRequestAggregateRoot(
                event.requestId(),
                event.submittedAt(),
                RequestStatus.REQUEST_SUBMITTED,
                pmCustomerInfo,
                pmOptions,
                null,
                null
        );

        pmQuoteRepo.save(pmAgg);

        log.info("PolicyManagement created quote request {} from CustomerSelfService event.", event.requestId());
    }

    @EventListener
    void on(CustomerDecisionSubmitted event) {
        decisionHandler.handleCustomerDecisionEvent(
                new CustomerDecisionEvent(event.decidedAt(), event.requestId(), event.accepted())
        );
    }
}
