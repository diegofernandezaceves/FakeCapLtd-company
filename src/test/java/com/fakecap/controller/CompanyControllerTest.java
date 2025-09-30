package com.fakecap.controller;

import com.fakecap.dto.CompanyDto;
import com.fakecap.repository.CompanyRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class CompanyControllerTest {

    @Inject
    Faker faker;

    @Inject
    CompanyRepository companyRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("Create a random company")
    public void givenRequestToCreateRandomCompanyWhenPostCompanyRandomThenShouldCreateACompany() {

        CompanyDto response = given()
                .when()
                .post("/company/random")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(CompanyDto.class);

        assertNotNull(response);
        assertNotNull(response.id());
        assertNotNull(response.name());
        assertNotNull(response.ticker());
        assertNotNull(response.address());
        assertNotNull(response.email());
        assertNotNull(response.sector());
        assertNotNull(response.country());
    }

    @Test
    @DisplayName("Get an existing company by id")
    public void givenExistingCompanyWhenGetCompanyByIdThenShouldReturnACompany() {

        CompanyDto createdCompany = createCompanyDto();

        CompanyDto retrievedCompany = given()
                .when()
                .get("/company/" + createdCompany.id())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(CompanyDto.class);

        assertEquals(createdCompany.id(), retrievedCompany.id());
        assertEquals(createdCompany.name(), retrievedCompany.name());
        assertEquals(createdCompany.ticker(), retrievedCompany.ticker());
        assertEquals(createdCompany.address(), retrievedCompany.address());
        assertEquals(createdCompany.email(), retrievedCompany.email());
        assertEquals(createdCompany.sector(), retrievedCompany.sector());
        assertEquals(createdCompany.country(), retrievedCompany.country());
    }

    @Test
    @DisplayName("Not found company by id")
    public void givenNonExistentCompanyIdWhenGetCompanyByIdThen404IsReturned() {
        given()
                .when()
                .get("/company/1")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Get all companies")
    public void givenMultipleCompaniesExistWhenGetAllCompaniesThenShouldReturnAllCompanies() {

        int number = faker.number().numberBetween(1, 5);

        List<CompanyDto> createdCompanies = Stream.generate(this::createCompanyDto)
                .limit(number)
                .sorted(Comparator.comparingLong(CompanyDto::id))
                .toList();

        List<CompanyDto> companies = given()
                .when()
                .get("/company")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .body()
                .jsonPath()
                .getList(".", CompanyDto.class);

        assertNotNull(companies);
        assertFalse(companies.isEmpty());
        assertEquals(companies.size(), number);

        for (int i = 0; i < createdCompanies.size(); i++) {

            CompanyDto createdCompany = createdCompanies.get(i);
            CompanyDto retrievedCompany = companies.get(i);

            assertEquals(createdCompany.id(), retrievedCompany.id());
            assertEquals(createdCompany.name(), retrievedCompany.name());
            assertEquals(createdCompany.ticker(), retrievedCompany.ticker());
            assertEquals(createdCompany.address(), retrievedCompany.address());
            assertEquals(createdCompany.email(), retrievedCompany.email());
            assertEquals(createdCompany.sector(), retrievedCompany.sector());
            assertEquals(createdCompany.country(), retrievedCompany.country());
        }
    }

    private CompanyDto createCompanyDto() {
        return given()
                .when()
                .post("/company/random")
                .then()
                .statusCode(200)
                .extract()
                .as(CompanyDto.class);
    }
}
