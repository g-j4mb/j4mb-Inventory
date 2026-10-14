package com.j4mb.ims.config;

import com.j4mb.ims.identity.application.port.out.LoadUserByEmailPort;
import com.j4mb.ims.identity.application.port.out.SaveUserPort;
import com.j4mb.ims.identity.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Configuration
@Profile("dev")
@ConditionalOnProperty(
        name = "security.bootstrap-admin.enabled",
        havingValue = "true"
)
public class AdminBootstrapConfiguration {

    @Bean
    public CommandLineRunner bootstrapAdmin(
            LoadUserByEmailPort loadUserByEmailPort,
            SaveUserPort saveUserPort,
            PasswordEncoder passwordEncoder,
            @Value("${security.bootstrap-admin.email}") String email,
            @Value("${security.bootstrap-admin.password}") String password
    ) {
        return args -> {
            if (!StringUtils.hasText(email)
                    || !StringUtils.hasText(password)) {
                throw new IllegalStateException(
                        "Bootstrap admin email and password must be configured"
                );
            }

            if (password.length() < 4) {
                throw new IllegalStateException(
                        "Bootstrap admin password must be at least 12 characters"
                );
            }

            String normalizedEmail = email.trim()
                    .toLowerCase(Locale.ROOT);

            if (loadUserByEmailPort.loadByEmail(normalizedEmail).isPresent()) {
                System.out.println(
                        "Bootstrap admin already exists; no changes made."
                );
                return;
            }

            User admin = new User(
                    null,
                    normalizedEmail,
                    passwordEncoder.encode(password),
                    "ADMIN",
                    true
            );

            saveUserPort.save(admin);

            System.out.println("Bootstrap admin created successfully.");
        };
    }
}