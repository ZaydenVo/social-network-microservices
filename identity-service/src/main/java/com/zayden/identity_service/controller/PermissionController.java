package com.zayden.identity_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.zayden.identity_service.dto.ApiResponse;
import com.zayden.identity_service.dto.request.PermissionRequest;
import com.zayden.identity_service.dto.response.PermissionResponse;
import com.zayden.identity_service.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping("/create")
    ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping("/getAll")
    ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.GetAllPermissions())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<String> deletePermission(@PathVariable String permission) {
        permissionService.deletePermission(permission);
        return ApiResponse.<String>builder()
                .result("Permission " + permission + " has been deleted!")
                .build();
    }
}
