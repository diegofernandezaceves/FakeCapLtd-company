package com.fakecap.repository;

import com.fakecap.model.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    public List<Order> findUnpublishOrders() {
        return list("published is not true");
    }

    @Transactional
    public void markAsPublished(List<String> ids) {
        update("published = true where id in ?1", ids);
    }

}
