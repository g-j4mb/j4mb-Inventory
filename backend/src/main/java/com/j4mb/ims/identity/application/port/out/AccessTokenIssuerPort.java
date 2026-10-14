package com.j4mb.ims.identity.application.port.out;

import java.time.Instant;

public interface AccessTokenIssuerPort {
    IssuedAccessToken issue(String email, String role);

    record IssuedAccessToken(String value, Instant expiresAt) {
    }
}
