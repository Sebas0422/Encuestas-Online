package com.example.encuestas_api.auth.infrastructure.adapter.out.crypto;

import com.example.encuestas_api.auth.application.port.out.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {
    private final PasswordEncoder encoder;

    public BCryptPasswordHasherAdapter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override public String hash(String rawPassword) { return encoder.encode(rawPassword); }
    @Override public boolean matches(String rawPassword, String passwordHash) { return encoder.matches(rawPassword, passwordHash); }
}
