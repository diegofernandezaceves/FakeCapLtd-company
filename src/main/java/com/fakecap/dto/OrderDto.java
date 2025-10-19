package com.fakecap.dto;

import java.math.BigDecimal;

public record OrderDto(String orderId, BigDecimal numberShares, boolean success) {

}
