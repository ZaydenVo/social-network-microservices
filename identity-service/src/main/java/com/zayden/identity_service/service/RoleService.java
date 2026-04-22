package com.zayden.identity_service.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.zayden.identity_service.dto.request.RoleRequest;
import com.zayden.identity_service.dto.response.RoleResponse;
import com.zayden.identity_service.entity.Permission;
import com.zayden.identity_service.entity.Role;
import com.zayden.identity_service.mapper.RoleMapper;
import com.zayden.identity_service.repository.PermissionRepository;
import com.zayden.identity_service.repository.RoleRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    public RoleResponse createRole(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public void deleteRole(String role) {
        roleRepository.deleteById(role);
    }
}
