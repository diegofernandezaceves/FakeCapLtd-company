package com.fakecap.service;

import com.fakecap.dto.CompanyDto;
import com.fakecap.model.Company;
import com.fakecap.repository.CompanyRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.java.Log;
import net.datafaker.Faker;

import java.util.List;

@Log
@ApplicationScoped
public class CompanyService {

    private final Faker faker;
    private final CompanyMapper companyMapper;
    private final CompanyRepository companyRepository;

    public CompanyService(Faker faker, CompanyMapper companyMapper, CompanyRepository companyRepository) {
        this.faker = faker;
        this.companyMapper = companyMapper;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CompanyDto createRandomCompany() {
        CompanyDto companyDto = new CompanyDto(
                faker.company().name(),
                faker.letterify("????", true),
                faker.address().fullAddress(),
                faker.internet().emailAddress(),
                faker.industrySegments().sector(),
                faker.country().name());

        Company company = this.companyMapper.toEntity(companyDto);
        this.companyRepository.persist(company);
        return this.companyMapper.toDto(company);
    }

    public CompanyDto getCompanyById(Long companyId) {
        return this.companyRepository.findByIdOptional(companyId)
                .map(this.companyMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    public CompanyDto getCompanyByTicker(String ticket) {
        return this.companyRepository.findByTicker(ticket)
                .map(this.companyMapper::toDto)
                .orElseThrow(NotFoundException::new);
    }

    public List<CompanyDto> getAllCompanies() {
        return this.companyRepository.findAll().stream()
                .map(this.companyMapper::toDto)
                .toList();
    }

}
