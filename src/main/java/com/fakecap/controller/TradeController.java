package com.fakecap.controller;

import com.fakecap.ShareRequest;
import com.fakecap.ShareResponse;
import com.fakecap.TradeOperation;
import com.fakecap.service.OrderService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

@GrpcService
public class TradeController implements TradeOperation {

    private final OrderService orderService;

    public TradeController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    @Blocking
    public Uni<ShareResponse> submit(ShareRequest shareRequest) {
        ShareResponse shareResponse = this.orderService.createOrder(shareRequest);
        return Uni.createFrom().item(shareResponse);
    }

}
