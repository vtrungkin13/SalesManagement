package pers.project.salesmanagement.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pers.project.salesmanagement.dto.request.CreateTenantRequest;
import pers.project.salesmanagement.dto.response.TenantResponse;
import pers.project.salesmanagement.service.TenantService;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
@Tag(name = "Tenant", description = "Tenant API")
public class TenantController {
    private final TenantService tenantService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantResponse> createTenant(@RequestBody CreateTenantRequest request) {
        TenantResponse response = tenantService.createTenant(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<TenantResponse>> getAllTenants() {
        List<TenantResponse> response = tenantService.getAllTenants();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
