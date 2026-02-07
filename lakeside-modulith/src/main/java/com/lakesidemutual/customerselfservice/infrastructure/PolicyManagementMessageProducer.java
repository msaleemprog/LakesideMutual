package com.lakesidemutual.customerselfservice.infrastructure;

import java.util.Date;
import java.util.Optional;

import org.microserviceapipatterns.domaindrivendesign.InfrastructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import org.springframework.context.ApplicationEventPublisher;
import com.lakesidemutual.customerselfservice.api.CustomerDecisionSubmitted;

import com.lakesidemutual.customerselfservice.domain.insurancequoterequest.CustomerDecisionEvent;
import com.lakesidemutual.customerselfservice.domain.insurancequoterequest.InsuranceQuoteRequestEvent;
import com.lakesidemutual.customerselfservice.interfaces.dtos.insurancequoterequest.InsuranceQuoteRequestDto;

@Profile("!migration")
@Component
public class PolicyManagementMessageProducer implements InfrastructureService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
private final ApplicationEventPublisher eventPublisher;

    @Value("${insuranceQuoteRequestEvent.queueName}")
    private String insuranceQuoteRequestEventQueue;

    @Value("${customerDecisionEvent.queueName}")
    private String customerDecisionEventQueue;

    /**
     * Optional on purpose: in modulith mode you might not have JMS configured,
     * and that must not crash REST endpoints.
     */
    private final Optional<JmsTemplate> jmsTemplate;

    public PolicyManagementMessageProducer(Optional<JmsTemplate> jmsTemplate,
                                       ApplicationEventPublisher eventPublisher) {
        this.jmsTemplate = jmsTemplate;
        this.eventPublisher = eventPublisher;
    }


    public void sendInsuranceQuoteRequest(Date date, InsuranceQuoteRequestDto insuranceQuoteRequestDto) {
        InsuranceQuoteRequestEvent event = new InsuranceQuoteRequestEvent(date, insuranceQuoteRequestDto);
        emitInsuranceQuoteRequestEvent(event);
    }

    public void sendCustomerDecision(Date date, Long insuranceQuoteRequestId, boolean quoteAccepted) {
        CustomerDecisionEvent event = new CustomerDecisionEvent(date, insuranceQuoteRequestId, quoteAccepted);
        emitCustomerDecisionEvent(event);
    }

    private void emitInsuranceQuoteRequestEvent(InsuranceQuoteRequestEvent event) {
        if (jmsTemplate.isEmpty()) {
            logger.warn("JMS is not configured (no JmsTemplate bean). Skipping InsuranceQuoteRequestEvent.");
            return;
        }

        try {
            jmsTemplate.get().convertAndSend(insuranceQuoteRequestEventQueue, event);
            logger.info("Successfully sent an insurance quote request to the Policy Management backend.");
        } catch (Exception exception) {
            // Important: never break the REST call because messaging is down in modulith mode
            logger.error("Failed to send insurance quote request event (ignored to keep REST working).", exception);
        }
    }

    private void emitCustomerDecisionEvent(CustomerDecisionEvent event) {

    // ✅ Always publish in-process for modulith so PM can update immediately
    // This matches Option A and works even if JMS is down/misconfigured.
    eventPublisher.publishEvent(new CustomerDecisionSubmitted(
    event.getInsuranceQuoteRequestId(),
    event.getDate(),
    event.isQuoteAccepted()
));


    // Keep JMS as best-effort (aligned with upstream), but never block REST
    if (jmsTemplate.isEmpty()) {
        logger.info("Published CustomerDecisionSubmitted (in-process). JMS not configured; skipping JMS send.");
        return;
    }

    try {
        jmsTemplate.get().convertAndSend(customerDecisionEventQueue, event);
        logger.info("Successfully sent a customer decision event to the Policy Management backend.");
    } catch (Exception exception) {
        logger.error("Failed to send customer decision event via JMS (in-process event already published).", exception);
    }
}

}
