package com.zayden.profile_service.service;

import com.zayden.profile_service.dto.request.ProfileCreationRequest;
import com.zayden.profile_service.dto.request.ProfileUpdateRequest;
import com.zayden.profile_service.dto.response.UserProfileResponse;
import com.zayden.profile_service.entity.UserProfile;
import com.zayden.profile_service.exception.AppException;
import com.zayden.profile_service.exception.ErrorCode;
import com.zayden.profile_service.mapper.UserProfileMapper;
import com.zayden.profile_service.repository.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        userProfileRepository.save(userProfile);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse updateProfile(ProfileUpdateRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userProfileRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        userProfileMapper.updateProfile(user, request);

        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(user));
    }
}
