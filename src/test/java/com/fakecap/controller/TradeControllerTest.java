package com.fakecap.controller;

import com.fakecap.OrderStatus;
import com.fakecap.ShareRequest;
import com.fakecap.ShareResponse;
import com.fakecap.TradeOperation;
import com.fakecap.dto.CompanyDto;
import com.fakecap.repository.CompanyRepository;
import com.fakecap.repository.OrderRepository;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class TradeControllerTest {

    @Inject
    Faker faker;

    @Inject
    CompanyRepository companyRepository;

    @Inject
    OrderRepository orderRepository;

    @GrpcClient
    TradeOperation tradeOperation;

    @BeforeEach
    @Transactional
    public void setUp() {
        orderRepository.deleteAll();
        companyRepository.deleteAll();
    }

    @Test
    @DisplayName("Create an order when company exists")
    public void givenShareRequestWhenCompanyExistsThenShouldReturnAShareResponseWithOrderId() {

        CompanyDto companyDto = this.createCompanyDto();
        ShareRequest shareRequest = this.createShareRequest(companyDto.ticker());

        ShareResponse shareResponse = tradeOperation.submit(shareRequest)
                .await().atMost(Duration.ofMillis(500));

        assertNotNull(shareResponse);
        assertNotNull(shareResponse.getOrderId());
        assertEquals(OrderStatus.SUCCESS, shareResponse.getStatus());
        assertNotNull(shareResponse.getErrorMessage());
        assertTrue(shareResponse.getErrorMessage().isEmpty());

    }

    @Test
    @DisplayName("Order is not created when company does not exist")
    public void givenShareRequestWhenCompanyDoeNotExistThenShouldReturnAShareResponseFailure() {

        ShareRequest shareRequest = this.createShareRequest(faker.name().name());

        ShareResponse shareResponse = tradeOperation.submit(shareRequest)
                .await().atMost(Duration.ofMillis(500));

        assertNotNull(shareResponse);
        assertNotNull(shareResponse.getOrderId());
        assertTrue(shareResponse.getOrderId().isEmpty());
        assertEquals(OrderStatus.FAILURE, shareResponse.getStatus());
        assertNotNull(shareResponse.getErrorMessage());
        assertTrue(shareResponse.getErrorMessage().contains("Not found company ticker: " + shareRequest.getTicker()));

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

    private ShareRequest createShareRequest(String ticker) {
        return ShareRequest.newBuilder()
                .setUserId(String.valueOf(this.faker.number().randomNumber()))
                .setTicker(ticker)
                .setInvestmentAmount(this.faker.number().randomDigit())
                .build();
    }
}
