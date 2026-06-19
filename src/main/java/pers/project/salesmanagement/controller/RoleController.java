package pers.project.salesmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.project.salesmanagement.dto.response.RoleResponse;
import pers.project.salesmanagement.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/get-all")
    public ResponseEntity<List<RoleResponse>> getAll(){
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.OK);
    }
}
