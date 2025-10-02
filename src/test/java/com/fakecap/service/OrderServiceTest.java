package com.fakecap.service;

import com.fakecap.OrderStatus;
import com.fakecap.ShareRequest;
import com.fakecap.ShareResponse;
import com.fakecap.model.Company;
import com.fakecap.model.Order;
import com.fakecap.repository.CompanyRepository;
import com.fakecap.repository.OrderRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.*;

@QuarkusTest
class OrderServiceTest {

    @Inject
    Faker faker;

    @Inject
    OrderMapper orderMapper;

    @Inject
    OrderService orderService;

    @InjectMock
    OrderRepository orderRepository;

    @InjectMock
    CompanyRepository companyRepository;

    @Test
    @DisplayName("Create an order")
    void givenShareRequestWhenCompanyExistsThenShouldCreateAnOrder() {

        String ticker = this.faker.name().name();
        ShareRequest shareRequest = this.createShareRequest(ticker);
        Company company = this.buildFakeCompany(ticker);
        Order order = this.orderMapper.toEntity(shareRequest);

        when(this.companyRepository.findByTicker(anyString())).thenReturn(Optional.of(company));
        doAnswer(invocationOnMock -> {
            Order orderToSave = invocationOnMock.getArgument(0);
            orderToSave.setId(UUID.randomUUID().toString());
            return null;
        }).when(this.orderRepository).persist(refEq(order, "id", "status", "company"));

        ShareResponse shareResponse = this.orderService.createOrder(shareRequest);

        assertNotNull(shareResponse);
        assertNotNull(shareResponse.getOrderId());
        assertDoesNotThrow(() -> UUID.fromString(shareResponse.getOrderId()));
        assertEquals(OrderStatus.SUCCESS, shareResponse.getStatus());
        assertNotNull(shareResponse.getErrorMessage());
        assertTrue(shareResponse.getErrorMessage().isEmpty());

    }

    @Test
    @DisplayName("Order does not created when company does not exist")
    void givenShareRequestWhenCompanyDoesNotExistsThenShouldNotCreateAnOrder() {

        when(this.companyRepository.findByTicker(anyString())).thenReturn(Optional.empty());

        ShareRequest shareRequest = createShareRequest();
        ShareResponse shareResponse = orderService.createOrder(shareRequest);

        assertNotNull(shareResponse);
        assertTrue(shareResponse.getOrderId().isEmpty());
        assertEquals(OrderStatus.FAILURE, shareResponse.getStatus());
        assertNotNull(shareResponse.getErrorMessage());
        assertTrue(shareResponse.getErrorMessage().contains("Not found company ticker: " + shareRequest.getTicker()));

        verifyNoInteractions(this.orderRepository);

    }

    private ShareRequest createShareRequest() {
        return createShareRequest(this.faker.name().name());
    }

    private ShareRequest createShareRequest(String ticker) {
        return ShareRequest.newBuilder()
                .setUserId(String.valueOf(this.faker.number().randomNumber()))
                .setTicker(ticker)
                .setInvestmentAmount(this.faker.number().randomDigit())
                .build();
    }

    private Company buildFakeCompany(String ticker) {
        return new Company(
                this.faker.number().randomNumber(),
                this.faker.company().name(),
                ticker,
                this.faker.address().fullAddress(),
                this.faker.internet().emailAddress(),
                this.faker.company().industry(),
                this.faker.country().name()
        );
    }
}