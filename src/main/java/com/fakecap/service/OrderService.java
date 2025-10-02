package com.fakecap.service;

import com.fakecap.ShareRequest;
import com.fakecap.ShareResponse;
import com.fakecap.model.Company;
import com.fakecap.model.Order;
import com.fakecap.model.OrderStatus;
import com.fakecap.repository.CompanyRepository;
import com.fakecap.repository.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.java.Log;

@Log
@ApplicationScoped
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;

    public OrderService(OrderMapper orderMapper, OrderRepository orderRepository, CompanyRepository companyRepository) {
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public ShareResponse createOrder(ShareRequest shareRequest) {

        try {

            Company company = this.getCompanyByTicker(shareRequest.getTicker());
            Order order = this.orderMapper.toEntity(shareRequest);

            order.setCompany(company);
            order.setStatus(OrderStatus.SUCCESS);

            this.orderRepository.persist(order);
            return this.orderMapper.toResponse(order);

        } catch (NotFoundException e) {
            return ShareResponse.newBuilder()
                    .setStatus(com.fakecap.OrderStatus.FAILURE)
                    .setErrorMessage(e.getMessage())
                    .build();
        }

    }

    private Company getCompanyByTicker(String ticker) {
        return this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> new NotFoundException("Not found company ticker: " + ticker));
    }

}
