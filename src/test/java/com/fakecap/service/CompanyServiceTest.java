package com.fakecap.service;

import com.fakecap.dto.CompanyDto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class CompanyServiceTest {

    @Inject
    Faker faker;

    @Inject
    CompanyService companyService;

    @Test
    @DisplayName("Create a random company")
    void givenRandomCompanyWhenCreateThenShouldCreateCompany() {

        CompanyDto randomCompany = companyService.createRandomCompany();

        assertNotNull(randomCompany.id());
        assertNotNull(randomCompany.name());
        assertNotNull(randomCompany.ticker());
        assertNotNull(randomCompany.address());
        assertNotNull(randomCompany.email());
        assertNotNull(randomCompany.sector());
        assertNotNull(randomCompany.country());

    }

    @Test
    @DisplayName("Get company by id")
    void givenCompanyIdWhenCompanyExistsThenShouldReturnCompany() {

        CompanyDto randomCompany = companyService.createRandomCompany();
        CompanyDto foundCompany = companyService.getCompanyById(randomCompany.id());

        assertEquals(randomCompany.id(), foundCompany.id());
        assertEquals(randomCompany.name(), foundCompany.name());
        assertEquals(randomCompany.ticker(), foundCompany.ticker());
        assertEquals(randomCompany.address(), foundCompany.address());
        assertEquals(randomCompany.email(), foundCompany.email());
        assertEquals(randomCompany.sector(), foundCompany.sector());
        assertEquals(randomCompany.country(), foundCompany.country());

    }

    @Test
    @DisplayName("Not found company by id")
    void givenCompanyIdWhenCompanyDoesNotExistThenShouldThrowException() {

        assertThrows(NotFoundException.class, () -> companyService.getCompanyById(faker.number().randomNumber()));

    }

    @Test
    @DisplayName("Get company by ticker")
    void givenCompanyTickerWhenCompanyExistsThenShouldReturnCompany() {

        CompanyDto randomCompany = companyService.createRandomCompany();
        CompanyDto foundCompany = companyService.getCompanyByTicker(randomCompany.ticker());

        assertEquals(randomCompany.id(), foundCompany.id());
        assertEquals(randomCompany.name(), foundCompany.name());
        assertEquals(randomCompany.ticker(), foundCompany.ticker());
        assertEquals(randomCompany.address(), foundCompany.address());
        assertEquals(randomCompany.email(), foundCompany.email());
        assertEquals(randomCompany.sector(), foundCompany.sector());
        assertEquals(randomCompany.country(), foundCompany.country());

    }

    @Test
    @DisplayName("Not found company by ticker")
    void givenCompanyTickerWhenCompanyDoesNotExistThenShouldThrowException() {

        assertThrows(NotFoundException.class, () -> companyService.getCompanyByTicker(faker.text().text()));

    }

    @Test
    @DisplayName("Get all companies")
    void givenSomeCompaniesWhenTheyExistThenShouldReturnAllCompanies() {

        int number = faker.number().numberBetween(0, 5);

        List<CompanyDto> companies = Stream.generate(() -> companyService.createRandomCompany())
                .limit(number)
                .toList();

        assertEquals(number, companies.size());

        companies.forEach(companyDto -> {

            CompanyDto foundCompany = companyService.getCompanyById(companyDto.id());
            assertEquals(companyDto.id(), foundCompany.id());
            assertEquals(companyDto.name(), foundCompany.name());
            assertEquals(companyDto.ticker(), foundCompany.ticker());
            assertEquals(companyDto.address(), foundCompany.address());
            assertEquals(companyDto.email(), foundCompany.email());
            assertEquals(companyDto.sector(), foundCompany.sector());
            assertEquals(companyDto.country(), foundCompany.country());

        });

    }

}