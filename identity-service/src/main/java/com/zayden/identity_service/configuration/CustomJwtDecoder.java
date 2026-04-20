package com.zayden.identity_service.configuration;

import com.nimbusds.jose.JOSEException;
import com.zayden.identity_service.dto.request.IntrospectRequest;
import com.zayden.identity_service.service.AuthenticationService;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomJwtDecoder implements JwtDecoder {
    @Autowired
    AuthenticationService authenticationService;

    @Value("${jwt.signerKey}")
    String SIGNER_KEY;

    NimbusJwtDecoder nimbusJwtDecoder;

    @PostConstruct
    public void init() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SIGNER_KEY.getBytes(), "HS512");

        nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspect(IntrospectRequest.builder().token(token).build());

            if (!response.isValid()) throw new JwtException("Invalid Token");
        } catch (ParseException | JOSEException e) {
            throw new JwtException(e.getMessage());
        }

        return nimbusJwtDecoder.decode(token);
    }
}
