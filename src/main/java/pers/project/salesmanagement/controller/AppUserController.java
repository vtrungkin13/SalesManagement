package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pers.project.salesmanagement.dto.request.UpdateAppUserRequest;
import pers.project.salesmanagement.dto.response.AppUserResponse;
import pers.project.salesmanagement.service.AppUserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/app-user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AppUserController {
    private final AppUserService appUserService;

    @PutMapping("/update")
    @PreAuthorize("#request.id() == authentication.principal.getId() " +
            "or hasRole('ADMIN')")
    public ResponseEntity<AppUserResponse> updateUser(@RequestBody UpdateAppUserRequest request) {
        return new ResponseEntity<>(appUserService.updateUser(request), HttpStatus.OK);
    }

    @PutMapping("/activate/{id}")
    public ResponseEntity<AppUserResponse> activateUser(@PathVariable UUID id) {
        return new ResponseEntity<>(appUserService.activateUser(id), HttpStatus.OK);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<AppUserResponse> deactivateUser(@PathVariable UUID id) {
        return new ResponseEntity<>(appUserService.deactivateUser(id), HttpStatus.OK);
    }
}
