package com.fakecap.service;

import com.fakecap.ShareRequest;
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
    @DisplayName("Retrieve, publish and delete order")
    void givenExistingOrdersWhenItHasNotBeenSentYetThenShouldPublishAndDeleteOrders() {

        Order order = this.createOrder();
        List<Order> orders = List.of(order);

        OrderDto sentOrder = this.createOrderDto(order);
        List<String> sentOrderIds = List.of(sentOrder.orderId());

        when(orderRepository.listAll()).thenReturn(orders);
        when(orderPublisher.publish(any(OrderDto.class))).thenReturn(CompletableFuture.completedFuture(sentOrder));
        doNothing().when(orderRepository).deleteByIds(sentOrderIds);

        this.orderProcessorService.processOrder();

        verify(orderPublisher, atLeastOnce()).publish(any(OrderDto.class));
        verify(orderRepository, atLeastOnce()).deleteByIds(sentOrderIds);
    }

    @Test
    @DisplayName("No order to publish and delete")
    void givenNoOrdersThenShouldNotPublishAndNotDeleteOrders() {

        when(orderRepository.listAll()).thenReturn(Collections.emptyList());

        this.orderProcessorService.processOrder();

        verify(orderPublisher, never()).publish(any(OrderDto.class));
        verify(orderRepository, never()).deleteByIds(any());
    }

    @Test
    @DisplayName("The order is not deleted when publishing fails")
    void givenExistingOrdersWhenPublishFailsThenShouldNotDeleteOrderAndHandlePublishErrorGracefully() {

        Order order = this.createOrder();
        List<Order> orders = List.of(order);

        when(orderRepository.listAll()).thenReturn(orders);
        when(orderPublisher.publish(any(OrderDto.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        this.orderProcessorService.processOrder();

        verify(orderPublisher, atLeastOnce()).publish(any(OrderDto.class));
        verify(orderRepository, never()).deleteByIds(any());
    }

    @Test
    @DisplayName("The error is handled gracefully when delete operation fails")
    void givenExistingOrdersWhenDeleteOperationFailsThenShouldHandleErrorGracefully() {

        Order order = this.createOrder();
        List<Order> orders = List.of(order);

        OrderDto sentOrder = this.createOrderDto(order);
        List<String> sentOrderIds = List.of(sentOrder.orderId());

        when(orderRepository.listAll()).thenReturn(orders);
        when(orderPublisher.publish(any(OrderDto.class))).thenReturn(CompletableFuture.completedFuture(sentOrder));
        doThrow(RuntimeException.class).when(orderRepository).deleteByIds(sentOrderIds);

        this.orderProcessorService.processOrder();

        verify(orderPublisher, atLeastOnce()).publish(any(OrderDto.class));
        verify(orderRepository, atLeastOnce()).deleteByIds(sentOrderIds);
    }

    private Order createOrder() {

        String orderId = UUID.randomUUID().toString();
        Long userId = faker.number().randomNumber();
        BigDecimal amount = new BigDecimal(faker.number().positive());

        return new Order(orderId, userId, new Company(), amount, SUCCESS, null);
    }

    private OrderDto createOrderDto(Order order) {
        boolean isSuccess = order.getStatus() == SUCCESS;
        return new OrderDto(order.getId(), order.getAmount(), isSuccess);
    }

}