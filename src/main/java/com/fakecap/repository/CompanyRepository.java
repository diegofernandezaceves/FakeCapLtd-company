package com.fakecap.repository;

import com.fakecap.model.Company;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class CompanyRepository implements PanacheRepository<Company> {

    public Optional<Company> findByTicker(String ticker){
        return find("ticker", ticker).firstResultOptional();
    }
}
