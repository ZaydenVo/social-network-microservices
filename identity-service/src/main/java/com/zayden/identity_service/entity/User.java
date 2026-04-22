package com.zayden.identity_service.entity;

import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userId;

    @Column(name = "username", unique = true, columnDefinition = "VARCHAR(225) COLLATE utf8mb4_unicode_ci")
    String username;

    String password;

    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(225) COLLATE utf8mb4_unicode_ci")
    String email;

    @Column(name = "email_verified", nullable = false, columnDefinition = "boolean default false")
    boolean emailVerified;

    @ManyToMany
    Set<Role> roles;
}
