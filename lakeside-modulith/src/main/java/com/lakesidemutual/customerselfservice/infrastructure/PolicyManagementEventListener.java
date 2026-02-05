package com.lakesidemutual.customerselfservice.infrastructure;

import java.util.Currency;
import java.util.Date;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.lakesidemutual.customerselfservice.api.InsuranceQuoteResponded;
import com.lakesidemutual.customerselfservice.api.PolicyCreated;
import com.lakesidemutual.customerselfservice.domain.insurancequoterequest.InsuranceQuoteEntity;
import com.lakesidemutual.customerselfservice.domain.insurancequoterequest.InsuranceQuoteRequestAggregateRoot;
import com.lakesidemutual.customerselfservice.domain.insurancequoterequest.MoneyAmount;

@Component
public class PolicyManagementEventListener {

    private final CustomerSelfServiceInsuranceQuoteRequestRepository repo;

    public PolicyManagementEventListener(CustomerSelfServiceInsuranceQuoteRequestRepository repo) {
        this.repo = repo;
    }

    @EventListener
    void on(InsuranceQuoteResponded event) {
        InsuranceQuoteRequestAggregateRoot req = repo.findById(event.requestId())
                .orElseThrow(() -> new IllegalStateException("Unknown quote request " + event.requestId()));

        Date when = event.respondedAt();

        if (event.requestAccepted()) {
            MoneyAmount premium = new MoneyAmount(event.insurancePremium().amount(),
                    Currency.getInstance(event.insurancePremium().currencyCode()));
            MoneyAmount limit = new MoneyAmount(event.policyLimit().amount(),
                    Currency.getInstance(event.policyLimit().currencyCode()));

            InsuranceQuoteEntity quote = new InsuranceQuoteEntity(event.expirationDate(), premium, limit);
            req.acceptRequest(quote, when); // REQUEST_SUBMITTED -> QUOTE_RECEIVED ✅
        } else {
            req.rejectRequest(when);        // REQUEST_SUBMITTED -> REQUEST_REJECTED ✅
        }

        repo.save(req);
    }

    @EventListener
    void on(PolicyCreated event) {
        InsuranceQuoteRequestAggregateRoot req = repo.findById(event.requestId())
                .orElseThrow(() -> new IllegalStateException("Unknown quote request " + event.requestId()));

        req.finalizeQuote(event.policyId(), event.createdAt()); // -> POLICY_CREATED ✅
        repo.save(req);
    }
}
