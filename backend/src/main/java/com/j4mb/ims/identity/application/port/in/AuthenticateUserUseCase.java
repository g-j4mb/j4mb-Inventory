package com.j4mb.ims.identity.application.port.in;

public interface AuthenticateUserUseCase {
    AuthenticationResult authenticate(String email, String password);

    record AuthenticationResult(
            String accessToken,
            String tokenType,
            long expiresIn
    ) {
    }
}
