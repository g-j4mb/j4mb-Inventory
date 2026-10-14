package com.j4mb.ims.identity.infrastructure.adapter.out.security;

import com.j4mb.ims.identity.application.port.out.PasswordHashVerifierPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHashVerifier
        implements PasswordHashVerifierPort {

    private final PasswordEncoder passwordEncoder;

    public BCryptPasswordHashVerifier(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }
}