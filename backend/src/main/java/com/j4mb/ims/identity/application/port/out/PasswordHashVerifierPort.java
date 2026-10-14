package com.j4mb.ims.identity.application.port.out;

public interface  PasswordHashVerifierPort {
    boolean matches(String rawPassword, String passwordHash);

}
