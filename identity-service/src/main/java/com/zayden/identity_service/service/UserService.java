package com.zayden.identity_service.service;

import com.zayden.identity_service.dto.request.UserCreationRequest;
import com.zayden.identity_service.dto.response.UserResponse;
import com.zayden.identity_service.entity.Role;
import com.zayden.identity_service.entity.User;
import com.zayden.identity_service.exception.AppException;
import com.zayden.identity_service.exception.ErrorCode;
import com.zayden.identity_service.mapper.UserMapper;
import com.zayden.identity_service.repository.RoleRepository;
import com.zayden.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserMapper userMapper;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findById("USER").orElseGet(() -> {
            Role role = Role.builder().name("USER").description("User Role").build();
            return roleRepository.save(role);
        });

        roles.add(userRole);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

}
