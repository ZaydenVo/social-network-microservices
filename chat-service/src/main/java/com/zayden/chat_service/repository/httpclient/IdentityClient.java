package com.zayden.chat_service.repository.httpclient;

import com.zayden.chat_service.dto.ApiResponse;
import com.zayden.chat_service.dto.request.IntrospectRequest;
import com.zayden.chat_service.dto.response.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "identity-service",
        url = "${app.services.identity.url}")
public interface IdentityClient {
    @PostMapping(value = "/auth/introspect", produces = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);
}
