package com.j4mb.ims.identity.infrastructure.adapter.in.web;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}