package com.j4mb.ims.identity.infrastructure.adapter.out.persistence;

import com.j4mb.ims.identity.application.port.out.LoadUserByEmailPort;
import com.j4mb.ims.identity.application.port.out.SaveUserPort;
import com.j4mb.ims.identity.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class UserPersistenceAdapter
        implements LoadUserByEmailPort, SaveUserPort {

    private final UserRepository repository;

    public UserPersistenceAdapter(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> loadByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        return repository.findByEmail(normalizedEmail)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity = UserJpaEntity.fromDomain(user);

        return repository.save(entity).toDomain();
    }
}