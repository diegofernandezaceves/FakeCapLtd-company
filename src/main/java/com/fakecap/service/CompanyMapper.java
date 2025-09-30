package com.fakecap.service;

import com.fakecap.dto.CompanyDto;
import com.fakecap.model.Company;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "jakarta-cdi",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    Company toEntity(CompanyDto companyDto);

    CompanyDto toDto(Company company);

}
