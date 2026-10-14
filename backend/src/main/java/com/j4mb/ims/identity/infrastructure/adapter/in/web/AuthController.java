package com.j4mb.ims.identity.infrastructure.adapter.in.web;

import com.j4mb.ims.identity.application.InvalidCredentialsException;
import com.j4mb.ims.identity.application.port.in.AuthenticateUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;

    public AuthController(
            AuthenticateUserUseCase authenticateUserUseCase
    ) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        try {
            var result = authenticateUserUseCase.authenticate(
                    request.email(),
                    request.password()
            );

            return ResponseEntity.ok(
                    new LoginResponse(
                            result.accessToken(),
                            result.tokenType(),
                            result.expiresIn()
                    )
            );
        } catch (InvalidCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "invalid_credentials",
                            "message", "Invalid email or password"
                    ));
        }
    }
}