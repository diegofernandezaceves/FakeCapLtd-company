package com.fakecap.repository;

import com.fakecap.model.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    @Transactional
    public void deleteByIds(List<String> ids) {
        delete("id in ?1", ids);
    }

}
