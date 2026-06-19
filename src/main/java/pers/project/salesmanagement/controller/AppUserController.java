package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.salesmanagement.dto.request.UpdateAppUserRequest;
import pers.project.salesmanagement.dto.response.AppUserResponse;
import pers.project.salesmanagement.service.AppUserService;

@RestController
@RequestMapping("/api/app-user")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;

    @PutMapping("/update")
    @PreAuthorize("#request.id() == authentication.principal.getId() " +
            "or hasRole('ADMIN')")
    public ResponseEntity<AppUserResponse> updateUser(@RequestBody UpdateAppUserRequest request) {
        return new ResponseEntity<>(appUserService.updateUser(request), HttpStatus.OK);
    }
}
