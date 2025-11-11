package com.fakecap.service;

import com.fakecap.dto.OrderDto;
import com.fakecap.messaging.OrderPublisher;
import com.fakecap.model.Company;
import com.fakecap.model.Order;
import com.fakecap.repository.OrderRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.fakecap.model.OrderStatus.SUCCESS;
import static org.mockito.Mockito.*;

@QuarkusTest
class OrderProcessorServiceTest {

    @Inject
    Faker faker;

    @Inject
    OrderProcessorService orderProcessorService;

    @InjectMock
    OrderPublisher orderPublisher;

    @InjectMock
    OrderRepository orderRepository;

    @Test
    @DisplayName("Retrieve, publish and mark as published order")
    void givenExistingOrdersWhenItHasNotBeenSentYetThenShouldPublishAndMarkAsPublishedOrders() {

        Order order = this.createOrder();
        List<Order> orders = List.of(order);

        OrderDto sentOrder = this.createOrderDto(order);
        List<String> sentOrderIds = List.of(sentOrder.orderId());

        when(orderRepository.findUnpublishOrders()).thenReturn(orders);
        when(orderPublisher.publish(any(OrderDto.class))).thenReturn(CompletableFuture.completedFuture(sentOrder));
        doNothing().when(orderRepository).markAsPublished(sentOrderIds);

        this.orderProcessorService.processOrder();

        verify(orderPublisher, atLeastOnce()).publish(any(OrderDto.class));
        verify(orderRepository, atLeastOnce()).markAsPublished(sentOrderIds);
    }

    @Test
    @DisplayName("No order to publish and mark as published")
    void givenNoOrdersThenShouldNotPublishAndNotMarkAsPublishedOrders() {

        when(orderRepository.findUnpublishOrders()).thenReturn(Collections.emptyList());

        this.orderProcessorService.processOrder();

        verify(orderPublisher, never()).publish(any(OrderDto.class));
        verify(orderRepository, never()).markAsPublished(any());
    }

    @Test
    @DisplayName("The order is not marked when publishing fails")
    void givenExistingOrdersWhenPublishFailsThenShouldNotMarkOrderAndHandlePublishErrorGracefully() {

        Order order = this.createOrder();
        List<Order> orders = List.of(order);

        when(orderRepository.findUnpublishOrders()).thenReturn(orders);
        when(orderPublisher.publish(any(OrderDto.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        this.orderProcessorService.processOrder();

        verify(orderPublisher, atLeastOnce()).publish(any(OrderDto.class));
        verify(orderRepository, never()).markAsPublished(any());
    }

    @Test
    @DisplayName("The error is handled gracefully when markAsPublished operation fails")
    void givenExistingOrdersWhenMarkAsPublishedOperationFailsThenShouldHandleErrorGracefully() {

        Order order = this.createOrder();
        List<Order> orders = List.of(order);

        OrderDto sentOrder = this.createOrderDto(order);
        List<String> sentOrderIds = List.of(sentOrder.orderId());

        when(orderRepository.findUnpublishOrders()).thenReturn(orders);
        when(orderPublisher.publish(any(OrderDto.class))).thenReturn(CompletableFuture.completedFuture(sentOrder));
        doThrow(RuntimeException.class).when(orderRepository).markAsPublished(sentOrderIds);

        this.orderProcessorService.processOrder();

        verify(orderPublisher, atLeastOnce()).publish(any(OrderDto.class));
        verify(orderRepository, atLeastOnce()).markAsPublished(sentOrderIds);
    }

    private Order createOrder() {

        String orderId = UUID.randomUUID().toString();
        Long userId = faker.number().randomNumber();
        BigDecimal amount = new BigDecimal(faker.number().positive());

        return new Order(orderId, userId, new Company(), amount, SUCCESS, null, null);
    }

    private OrderDto createOrderDto(Order order) {
        boolean isSuccess = order.getStatus() == SUCCESS;
        return new OrderDto(order.getId(), order.getAmount(), isSuccess);
    }

}