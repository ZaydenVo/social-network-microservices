package com.zayden.chat_service.repository.httpclient;

import com.zayden.chat_service.configuration.AuthenticationRequestInterceptor;
import com.zayden.chat_service.dto.ApiResponse;
import com.zayden.chat_service.dto.response.UserProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "profile-service",
        url = "${app.services.profile.url}")
public interface ProfileClient {
    @GetMapping(value = "/internal/users/{userId}")
    ApiResponse<UserProfileResponse> getUserProfile(@PathVariable String userId);
}
