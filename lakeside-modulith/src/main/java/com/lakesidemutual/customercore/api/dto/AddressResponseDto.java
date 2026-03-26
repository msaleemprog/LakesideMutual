package com.lakesidemutual.customercore.api.dto;

public class AddressResponseDto {
    private final String streetAddress;
    private final String postalCode;
    private final String city;

    public AddressResponseDto(String streetAddress, String postalCode, String city) {
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.city = city;
    }

    public String getStreetAddress() { return streetAddress; }
    public String getPostalCode() { return postalCode; }
    public String getCity() { return city; }
}
