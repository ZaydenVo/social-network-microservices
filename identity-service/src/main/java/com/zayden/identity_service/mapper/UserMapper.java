package com.zayden.identity_service.mapper;

import org.mapstruct.Mapper;

import com.zayden.identity_service.dto.request.UserCreationRequest;
import com.zayden.identity_service.dto.response.UserResponse;
import com.zayden.identity_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);
}
