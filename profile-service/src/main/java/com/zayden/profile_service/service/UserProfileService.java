package com.zayden.profile_service.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.zayden.profile_service.dto.request.ProfileCreationRequest;
import com.zayden.profile_service.dto.request.ProfileUpdateRequest;
import com.zayden.profile_service.dto.response.UserProfileResponse;
import com.zayden.profile_service.entity.UserProfile;
import com.zayden.profile_service.exception.AppException;
import com.zayden.profile_service.exception.ErrorCode;
import com.zayden.profile_service.mapper.UserProfileMapper;
import com.zayden.profile_service.repository.UserProfileRepository;
import com.zayden.profile_service.repository.httpclient.FileClient;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileService {
    UserProfileRepository userProfileRepository;
    UserProfileMapper userProfileMapper;
    FileClient fileClient;

    public UserProfileResponse createProfile(ProfileCreationRequest request) {
        UserProfile userProfile = userProfileMapper.toUserProfile(request);
        userProfileRepository.save(userProfile);
        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse updateProfile(ProfileUpdateRequest request) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        userProfileMapper.updateProfile(user, request);

        return userProfileMapper.toUserProfileResponse(userProfileRepository.save(user));
    }

    public UserProfileResponse updateAvatar(MultipartFile file) {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var userProfile = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        try {
            userProfile.setAvatar(fileClient.uploadMedia(file).getResult().getUrl());
            userProfileRepository.save(userProfile);
        } catch (FeignException e) {
            throw new AppException(ErrorCode.CANNOT_UPDATE_AVATAR);
        }

        return userProfileMapper.toUserProfileResponse(userProfile);
    }

    public UserProfileResponse getMyInfo() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));

        return userProfileMapper.toUserProfileResponse(user);
    }

    public UserProfileResponse getProfileByUsername(String username) {
        var user = userProfileRepository
                .findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));
        return userProfileMapper.toUserProfileResponse(user);
    }

    public UserProfileResponse getProfileByUserId(String userId) {
        var user = userProfileRepository
                .findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_EXISTED));
        return userProfileMapper.toUserProfileResponse(user);
    }
}
