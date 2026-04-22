package com.zayden.identity_service.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.zayden.identity_service.dto.request.AuthenticationResquest;
import com.zayden.identity_service.dto.request.IntrospectRequest;
import com.zayden.identity_service.dto.request.LogoutRequest;
import com.zayden.identity_service.dto.request.RefreshTokenRequest;
import com.zayden.identity_service.dto.response.AuthenticationResponse;
import com.zayden.identity_service.dto.response.IntrospectResponse;
import com.zayden.identity_service.entity.InvalidatedToken;
import com.zayden.identity_service.entity.User;
import com.zayden.identity_service.exception.AppException;
import com.zayden.identity_service.exception.ErrorCode;
import com.zayden.identity_service.repository.InvalidatedTokenRepository;
import com.zayden.identity_service.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public AuthenticationResponse authenticate(AuthenticationResquest resquest) {
        var user = userRepository
                .findByUsername(resquest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(resquest.getPassword(), user.getPassword()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return AuthenticationResponse.builder()
                .token(generateToken(user))
                .authenticated(true)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        SignedJWT jwt = null;
        boolean isValid = true;
        try {
            jwt = verifyToken(request.getToken(), false);
            if (invalidatedTokenRepository.existsById(jwt.getJWTClaimsSet().getJWTID())) isValid = false;
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .userId(Objects.nonNull(jwt) ? jwt.getJWTClaimsSet().getSubject() : null)
                .valid(isValid)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(request.getToken(), true);

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(signToken.getJWTClaimsSet().getJWTID())
                    .expiryTime(signToken.getJWTClaimsSet().getExpirationTime())
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token already expired!");
        }
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken(), true);

        if (invalidatedTokenRepository.existsById(signToken.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(signToken.getJWTClaimsSet().getJWTID())
                .expiryTime(signToken.getJWTClaimsSet().getExpirationTime())
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

        var username = signToken.getJWTClaimsSet().getSubject();
        User user = userRepository.findById(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(generateToken(user))
                .build();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("zayden.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());

                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = isRefresh
                ? new Date(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!signedJWT.verify(verifier) || !expiryTime.after(new Date()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }
}
