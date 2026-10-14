package com.j4mb.ims.identity.infrastructure.adapter.out.persistence;

import com.j4mb.ims.identity.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class UserJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "role", nullable = false, length = 30)
    private String role;

    @Column(name = "active", nullable = false)
    private boolean active;

    protected UserJpaEntity() {
        // Required by JPA.
    }

    private UserJpaEntity(
            String email,
            String passwordHash,
            String role,
            boolean active
    ) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = active;
    }

    public static UserJpaEntity fromDomain(User user) {
        return new UserJpaEntity(
                user.email(),
                user.passwordHash(),
                user.role(),
                user.active()
        );
    }

    public User toDomain() {
        return new User(
                id,
                email,
                passwordHash,
                role,
                active
        );
    }
}