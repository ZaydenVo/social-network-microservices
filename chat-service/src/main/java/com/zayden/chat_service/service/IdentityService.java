package com.zayden.chat_service.service;

import com.zayden.chat_service.dto.request.IntrospectRequest;
import com.zayden.chat_service.dto.response.IntrospectResponse;
import com.zayden.chat_service.repository.httpclient.IdentityClient;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityService {
    IdentityClient identityClient;

    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            var result = identityClient.introspect(request).getResult();
            if (Objects.isNull(result))
                return IntrospectResponse.builder().valid(false).build();
            return result;
        } catch (FeignException e) {
            return IntrospectResponse.builder().valid(false).build();
        }
    }
}
