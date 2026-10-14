package com.j4mb.ims.identity.application.service;

import com.j4mb.ims.identity.application.InvalidCredentialsException;
import com.j4mb.ims.identity.application.port.in.AuthenticateUserUseCase;
import com.j4mb.ims.identity.application.port.out.AccessTokenIssuerPort;
import com.j4mb.ims.identity.application.port.out.LoadUserByEmailPort;
import com.j4mb.ims.identity.application.port.out.PasswordHashVerifierPort;
import com.j4mb.ims.identity.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AuthenticateUserService implements AuthenticateUserUseCase {

    private final LoadUserByEmailPort loadUserByEmailPort;
    private final PasswordHashVerifierPort passwordHashVerifierPort;
    private final AccessTokenIssuerPort accessTokenIssuerPort;

    public AuthenticateUserService(
            LoadUserByEmailPort loadUserByEmailPort,
            PasswordHashVerifierPort passwordHashVerifierPort,
            AccessTokenIssuerPort accessTokenIssuerPort
    ) {
        this.loadUserByEmailPort = loadUserByEmailPort;
        this.passwordHashVerifierPort = passwordHashVerifierPort;
        this.accessTokenIssuerPort = accessTokenIssuerPort;
    }

    @Override
    public AuthenticationResult authenticate(String email, String password) {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new InvalidCredentialsException();
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        User user = loadUserByEmailPort.loadByEmail(normalizedEmail)
                .orElseThrow(InvalidCredentialsException::new);

        if (!user.active()
                || !passwordHashVerifierPort.matches(
                password,
                user.passwordHash()
        )) {
            throw new InvalidCredentialsException();
        }

        var token = accessTokenIssuerPort.issue(user.email(), user.role());

        long expiresIn = Math.max(
                0,
                token.expiresAt().getEpochSecond()
                        - java.time.Instant.now().getEpochSecond()
        );

        return new AuthenticationResult(
                token.value(),
                "Bearer",
                expiresIn
        );
    }
}