package com.fakecap.messaging;

import com.fakecap.dto.OrderDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class OrderPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderPublisher.class);

    private final Emitter<OrderDto> orderEmitter;

    public OrderPublisher(@Channel("order-processor") Emitter<OrderDto> orderEmitter) {
        this.orderEmitter = orderEmitter;
    }

    public CompletableFuture<OrderDto> publish(OrderDto order) {
        return this.orderEmitter.send(order)
                        .toCompletableFuture()
                        .thenAccept(v -> log.info("🚀 Published order: {}", order))
                        .thenApply(v -> order);
    }

}
