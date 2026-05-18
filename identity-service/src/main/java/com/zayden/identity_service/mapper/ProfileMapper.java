package com.zayden.identity_service.mapper;

import org.mapstruct.Mapper;

import com.zayden.identity_service.dto.request.ProfileCreationRequest;
import com.zayden.identity_service.dto.request.UserCreationRequest;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileCreationRequest toProfileCreationRequest(UserCreationRequest request);
}
