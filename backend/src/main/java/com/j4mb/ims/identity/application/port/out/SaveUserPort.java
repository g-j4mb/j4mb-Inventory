package com.j4mb.ims.identity.application.port.out;

import com.j4mb.ims.identity.domain.User;

public interface SaveUserPort {

    User save(User user);
}