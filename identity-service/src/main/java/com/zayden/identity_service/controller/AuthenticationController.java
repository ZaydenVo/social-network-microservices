package com.zayden.identity_service.controller;

import com.nimbusds.jose.JOSEException;
import com.zayden.identity_service.dto.ApiResponse;
import com.zayden.identity_service.dto.request.AuthenticationResquest;
import com.zayden.identity_service.dto.request.IntrospectRequest;
import com.zayden.identity_service.dto.request.LogoutRequest;
import com.zayden.identity_service.dto.response.AuthenticationResponse;
import com.zayden.identity_service.dto.response.IntrospectResponse;
import com.zayden.identity_service.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationResquest resquest) {
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticationService.authenticate(resquest))
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        return ApiResponse.<IntrospectResponse>builder()
                .result(authenticationService.introspect(request))
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<String> logout(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<String>builder()
                .result("You have been logged out successfully!")
                .build();
    }
}
