package com.zayden.identity_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.zayden.identity_service.dto.request.PermissionRequest;
import com.zayden.identity_service.dto.response.PermissionResponse;
import com.zayden.identity_service.entity.Permission;
import com.zayden.identity_service.mapper.PermissionMapper;
import com.zayden.identity_service.repository.PermissionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> GetAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public void deletePermission(String permission) {
        permissionRepository.deleteById(permission);
    }
}
