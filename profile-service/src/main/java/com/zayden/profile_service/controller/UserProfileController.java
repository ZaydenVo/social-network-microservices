package com.zayden.profile_service.controller;

import com.zayden.profile_service.dto.ApiResponse;
import com.zayden.profile_service.dto.request.ProfileUpdateRequest;
import com.zayden.profile_service.dto.response.UserProfileResponse;
import com.zayden.profile_service.service.UserProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
    UserProfileService userProfileService;

    @PutMapping("/users/update")
    ApiResponse<UserProfileResponse> updateProfile(@RequestBody ProfileUpdateRequest request) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateProfile(request))
                .build();
    }

    @PutMapping("/users/avatar")
    ApiResponse<UserProfileResponse> updateAvatar(@RequestParam("file") MultipartFile file) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.updateAvatar(file))
                .build();
    }

    @GetMapping("/users/my-info")
    ApiResponse<UserProfileResponse> getMyInfo() {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getMyInfo())
                .build();
    }

    @GetMapping("/users/profile/{username}")
    ApiResponse<UserProfileResponse> getProfilebyUsername(@PathVariable String username) {
        return ApiResponse.<UserProfileResponse>builder()
                .result(userProfileService.getProfileByUsername(username))
                .build();
    }
}
