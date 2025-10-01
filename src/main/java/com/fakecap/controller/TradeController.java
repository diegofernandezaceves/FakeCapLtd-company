package com.fakecap.controller;

import com.fakecap.ShareRequest;
import com.fakecap.ShareResponse;
import com.fakecap.TradeOperation;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

@GrpcService
public class TradeController implements TradeOperation {

    @Override
    public Uni<ShareResponse> submit(ShareRequest shareRequest) {
        return Uni.createFrom().nullItem();
    }

}
