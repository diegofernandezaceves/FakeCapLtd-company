package com.fakecap.service;

import com.fakecap.dto.OrderDto;
import com.fakecap.messaging.OrderPublisher;
import com.fakecap.repository.OrderRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class OrderProcessorService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessorService.class);

    private final Faker faker;
    private final OrderPublisher orderPublisher;
    private final OrderRepository orderRepository;

    public OrderProcessorService(Faker faker, OrderPublisher orderPublisher, OrderRepository orderRepository) {
        this.faker = faker;
        this.orderPublisher = orderPublisher;
        this.orderRepository = orderRepository;
    }

    @Scheduled(every = "10s")
    @Transactional
    public void processOrder() {
        List<OrderDto> orders = getAllAndBuildOrders();
        List<CompletableFuture<OrderDto>> futuresOrderSent = this.publishOrders(orders);
        this.deleteOrderPublished(futuresOrderSent);
    }

    private List<OrderDto> getAllAndBuildOrders() {
        List<OrderDto> orders = this.orderRepository.listAll().stream()
                .map(order -> {
                    boolean isSuccess = faker.bool().bool();
                    return new OrderDto(order.getId(), order.getAmount(), isSuccess);
                })
                .toList();
        log.debug("🔍 Found {} orders: {}", orders.size(), orders);
        return orders;
    }

    private void deleteOrderPublished(List<CompletableFuture<OrderDto>> futuresOrderSent) {
        this.joinAllFutures(futuresOrderSent)
                .thenAccept(successfulOrders -> {
                    if (!successfulOrders.isEmpty()) {
                        List<String> orderIds = successfulOrders.stream().map(OrderDto::orderId).toList();
                        orderRepository.deleteByIds(orderIds);
                        log.debug("✅ Deleted {} successfully published orders: {}", orderIds.size(), successfulOrders);
                    }
                })
                .exceptionally(throwable -> {
                    log.error("❌ Error processing orders: {}", throwable.getMessage());
                    return null;
                });
    }

    private List<CompletableFuture<OrderDto>> publishOrders(List<OrderDto> orders) {
        return orders.stream()
                .map(this.orderPublisher::publish)
                .toList();
    }

    private <T> CompletableFuture<List<T>> joinAllFutures(List<CompletableFuture<T>> completableFutures) {
        return CompletableFuture
                .allOf(completableFutures.toArray(CompletableFuture[]::new))
                .thenApply(v -> completableFutures.stream()
                        .map(CompletableFuture::join)
                        .toList());
    }

}
