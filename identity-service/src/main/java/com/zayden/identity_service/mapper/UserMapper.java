package com.zayden.identity_service.mapper;

import com.zayden.identity_service.dto.request.UserCreationRequest;
import com.zayden.identity_service.dto.response.UserResponse;
import com.zayden.identity_service.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);
}
