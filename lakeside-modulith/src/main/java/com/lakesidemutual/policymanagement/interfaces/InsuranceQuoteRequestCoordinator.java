package com.lakesidemutual.policymanagement.interfaces;

import java.util.Currency;
import java.util.Date;

import jakarta.validation.Valid;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lakesidemutual.customerselfservice.api.InsuranceQuoteResponded;
import com.lakesidemutual.customerselfservice.api.Money;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.InsuranceQuoteEntity;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.InsuranceQuoteRequestAggregateRoot;
import com.lakesidemutual.policymanagement.domain.insurancequoterequest.RequestStatus;
import com.lakesidemutual.policymanagement.domain.policy.MoneyAmount;
import com.lakesidemutual.policymanagement.infrastructure.PolicyInsuranceQuoteRequestRepository;
import com.lakesidemutual.policymanagement.interfaces.dtos.insurancequoterequest.InsuranceQuoteRequestNotFoundException;
import com.lakesidemutual.policymanagement.interfaces.dtos.insurancequoterequest.InsuranceQuoteResponseDto;

@RestController
@RequestMapping("/policy-management/insurance-quote-requests")
public class InsuranceQuoteRequestCoordinator {

    private final PolicyInsuranceQuoteRequestRepository repo;
    private final ApplicationEventPublisher events;

    public InsuranceQuoteRequestCoordinator(PolicyInsuranceQuoteRequestRepository repo, ApplicationEventPublisher events) {
        this.repo = repo;
        this.events = events;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        InsuranceQuoteRequestAggregateRoot req = repo.findById(id)
                .orElseThrow(() -> new InsuranceQuoteRequestNotFoundException("No quote request with id " + id));
        return ResponseEntity.ok(com.lakesidemutual.policymanagement.interfaces.dtos.insurancequoterequest.InsuranceQuoteRequestDto.fromDomainObject(req));
    }

    /**
     * This is the missing step in your modulith.
     * It moves REQUEST_SUBMITTED -> QUOTE_RECEIVED (or REQUEST_REJECTED),
     * then publishes InsuranceQuoteResponded for CustomerSelfService.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> respond(@PathVariable Long id, @Valid @RequestBody InsuranceQuoteResponseDto dto) {

        InsuranceQuoteRequestAggregateRoot req = repo.findById(id)
                .orElseThrow(() -> new InsuranceQuoteRequestNotFoundException("No quote request with id " + id));

        Date now = new Date();

        if (RequestStatus.QUOTE_RECEIVED.toString().equals(dto.getStatus())) {
            // require quote fields
            MoneyAmount premium = dto.getInsurancePremium().toDomainObject();
            MoneyAmount limit = dto.getPolicyLimit().toDomainObject();

            InsuranceQuoteEntity quote = new InsuranceQuoteEntity(dto.getExpirationDate(), premium, limit);
            req.acceptRequest(quote, now);
            repo.save(req);

            events.publishEvent(new InsuranceQuoteResponded(
                    id, now, true,
                    quote.getExpirationDate(),
                    new Money(quote.getInsurancePremium().getAmount(), quote.getInsurancePremium().getCurrency().getCurrencyCode()),
                    new Money(quote.getPolicyLimit().getAmount(), quote.getPolicyLimit().getCurrency().getCurrencyCode())
            ));

        } else if (RequestStatus.REQUEST_REJECTED.toString().equals(dto.getStatus())) {

            req.rejectRequest(now);
            repo.save(req);

            events.publishEvent(new InsuranceQuoteResponded(
                    id, now, false,
                    null, null, null
            ));

        } else {
            throw new RuntimeException("Unsupported status for policy-management response: " + dto.getStatus()
                    + " (use QUOTE_RECEIVED or REQUEST_REJECTED)");
        }

        return ResponseEntity.ok(com.lakesidemutual.policymanagement.interfaces.dtos.insurancequoterequest.InsuranceQuoteRequestDto.fromDomainObject(req));
    }
}
