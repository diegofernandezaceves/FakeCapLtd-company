package com.fakecap.service;

import com.fakecap.ShareRequest;
import com.fakecap.ShareResponse;
import com.fakecap.model.Order;
import org.mapstruct.*;

@Mapper(
        componentModel = "jakarta-cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    @Mapping(source = "investmentAmount", target = "amount")
    Order toEntity(ShareRequest shareRequest);

    @Mapping(source = "id", target = "orderId")
    @Mapping(target = "errorMessage", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    ShareResponse toResponse(Order order);

}
