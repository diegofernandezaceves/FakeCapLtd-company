package com.fakecap.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CompanyDto(
        Long id,
        String name,
        String ticker,
        String address,
        String email,
        String sector,
        String country) {

    public CompanyDto(String name, String ticker, String address, String email, String sector, String country) {
        this(null, name, ticker, address, email, sector, country);
    }
}
