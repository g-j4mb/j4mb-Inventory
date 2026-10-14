package com.j4mb.ims.identity.application.port.out;

import com.j4mb.ims.identity.domain.User;

import java.util.Optional;

public interface LoadUserByEmailPort {

    Optional<User> loadByEmail(String email);
}