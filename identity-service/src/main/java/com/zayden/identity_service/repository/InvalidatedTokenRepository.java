package com.zayden.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zayden.identity_service.entity.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
