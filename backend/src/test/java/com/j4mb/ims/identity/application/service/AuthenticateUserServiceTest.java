
package com.j4mb.ims.identity.application.service;

import com.j4mb.ims.identity.application.InvalidCredentialsException;
import com.j4mb.ims.identity.application.port.out.AccessTokenIssuerPort;
import com.j4mb.ims.identity.application.port.out.LoadUserByEmailPort;
import com.j4mb.ims.identity.application.port.out.PasswordHashVerifierPort;
import com.j4mb.ims.identity.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserServiceTest {

    @Mock
    private LoadUserByEmailPort loadUserByEmailPort;

    @Mock
    private PasswordHashVerifierPort passwordHashVerifierPort;

    @Mock
    private AccessTokenIssuerPort accessTokenIssuerPort;

    private AuthenticateUserService authenticateUserService;

    @BeforeEach
    void setUp() {
        authenticateUserService = new AuthenticateUserService(
                loadUserByEmailPort,
                passwordHashVerifierPort,
                accessTokenIssuerPort
        );
    }

    @Test
    void shouldAuthenticateActiveUserWithCorrectPassword() {
        User user = new User(
                1L,
                "admin@example.com",
                "$2a$12$exampleHash",
                "ADMIN",
                true
        );

        when(loadUserByEmailPort.loadByEmail("admin@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordHashVerifierPort.matches(
                "CorrectPassword123!",
                user.passwordHash()
        )).thenReturn(true);

        when(accessTokenIssuerPort.issue(
                user.email(),
                user.role()
        )).thenReturn(new AccessTokenIssuerPort.IssuedAccessToken(
                "test-access-token",
                Instant.now().plusSeconds(900)
        ));

        var result = authenticateUserService.authenticate(
                "ADMIN@example.com",
                "CorrectPassword123!"
        );

        assertEquals("test-access-token", result.accessToken());
        assertEquals("Bearer", result.tokenType());
        verify(loadUserByEmailPort).loadByEmail("admin@example.com");
        verify(passwordHashVerifierPort).matches(
                "CorrectPassword123!",
                user.passwordHash()
        );
        verify(accessTokenIssuerPort).issue("admin@example.com", "ADMIN");
    }

    @Test
    void shouldRejectIncorrectPassword() {
        User user = new User(
                1L,
                "admin@example.com",
                "$2a$12$exampleHash",
                "ADMIN",
                true
        );

        when(loadUserByEmailPort.loadByEmail("admin@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordHashVerifierPort.matches(
                "WrongPassword123!",
                user.passwordHash()
        )).thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserService.authenticate(
                        "admin@example.com",
                        "WrongPassword123!"
                )
        );

        verify(accessTokenIssuerPort, never()).issue(anyString(), anyString());
    }

    @Test
    void shouldRejectUnknownEmail() {
        when(loadUserByEmailPort.loadByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserService.authenticate(
                        "unknown@example.com",
                        "SomePassword123!"
                )
        );

        verifyNoInteractions(passwordHashVerifierPort, accessTokenIssuerPort);
    }

    @Test
    void shouldRejectDisabledUserEvenWithCorrectPassword() {
        User user = new User(
                2L,
                "disabled@example.com",
                "$2a$12$exampleHash",
                "USER",
                false
        );

        when(loadUserByEmailPort.loadByEmail("disabled@example.com"))
                .thenReturn(Optional.of(user));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserService.authenticate(
                        "disabled@example.com",
                        "CorrectPassword123!"
                )
        );

        verifyNoInteractions(passwordHashVerifierPort, accessTokenIssuerPort);
    }

    @Test
    void shouldRejectBlankEmail() {
        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserService.authenticate(
                        " ",
                        "SomePassword123!"
                )
        );

        verifyNoInteractions(
                loadUserByEmailPort,
                passwordHashVerifierPort,
                accessTokenIssuerPort
        );
    }

    @Test
    void shouldRejectBlankPassword() {
        assertThrows(
                InvalidCredentialsException.class,
                () -> authenticateUserService.authenticate(
                        "admin@example.com",
                        " "
                )
        );

        verifyNoInteractions(
                loadUserByEmailPort,
                passwordHashVerifierPort,
                accessTokenIssuerPort
        );
    }
}
