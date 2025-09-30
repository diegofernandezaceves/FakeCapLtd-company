package com.fakecap.controller;

import com.fakecap.dto.CompanyDto;
import com.fakecap.service.CompanyService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

@Path("/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @POST
    @Path("random")
    @Produces(MediaType.APPLICATION_JSON)
    public CompanyDto createRandomCompany() {
        return this.companyService.createRandomCompany();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public CompanyDto getCompanyById(@PathParam("id") long id) {
        return this.companyService.getCompanyById(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CompanyDto> getAllCompanies() {
        return this.companyService.getAllCompanies();
    }

}
