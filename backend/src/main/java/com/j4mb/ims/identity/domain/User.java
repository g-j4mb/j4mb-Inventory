package com.j4mb.ims.identity.domain;

import java.util.Locale;
import java.util.Objects;

public record User(
        Long id,
        String email,
        String passwordHash,
        String role,
        boolean active
) {
    public User {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        Objects.requireNonNull(role, "role must not be null");

        email = email.trim().toLowerCase(Locale.ROOT);
        role = role.trim().toUpperCase(Locale.ROOT);

        if (email.isBlank()) {
            throw new IllegalArgumentException("email must not be blank");
        }

        if (passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash must not be blank");
        }


    }
}