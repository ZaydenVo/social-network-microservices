package com.zayden.identity_service.controller;

import com.zayden.identity_service.dto.ApiResponse;
import com.zayden.identity_service.dto.request.RoleRequest;
import com.zayden.identity_service.dto.response.RoleResponse;
import com.zayden.identity_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping("/create")
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping("/getAll")
    ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<String> deleteRole(@PathVariable String role) {
        roleService.deleteRole(role);
        return ApiResponse.<String>builder()
                .result("Role " + role + " has been deleted!")
                .build();
    }
}
