package com.lakesidemutual.policymanagement.domain.insurancequoterequest;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.lakesidemutual.policymanagement.domain.customer.CustomerId;

/**
 * CustomerInfoEntity is an entity that is part of an InsuranceQuoteRequestAggregateRoot
 * and contains infos about the initiator of the request.
 */
@Entity(name = "PolicyManagementCustomerInfo")
@Table(name = "pm_customerinfos", schema = "POLICYMANAGEMENT")
public class CustomerInfoEntity implements org.microserviceapipatterns.domaindrivendesign.Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "customer_id"))
    })
    private final CustomerId customerId;

    private final String firstname;

    private final String lastname;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "streetAddress", column = @Column(name = "contact_street_address")),
        @AttributeOverride(name = "postalCode",    column = @Column(name = "contact_postal_code")),
        @AttributeOverride(name = "city",          column = @Column(name = "contact_city"))
    })
    private final Address contactAddress;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "streetAddress", column = @Column(name = "billing_street_address")),
        @AttributeOverride(name = "postalCode",    column = @Column(name = "billing_postal_code")),
        @AttributeOverride(name = "city",          column = @Column(name = "billing_city"))
    })
    private final Address billingAddress;

    // JPA needs a no-args constructor
    protected CustomerInfoEntity() {
        this.customerId = null;
        this.firstname = null;
        this.lastname = null;
        this.contactAddress = null;
        this.billingAddress = null;
    }

    public CustomerInfoEntity(CustomerId customerId,
                              String firstname,
                              String lastname,
                              Address contactAddress,
                              Address billingAddress) {
        this.customerId = customerId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.contactAddress = contactAddress;
        this.billingAddress = billingAddress;
    }

    public Long getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public Address getContactAddress() {
        return contactAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }
}
