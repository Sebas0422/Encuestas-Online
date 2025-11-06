package com.example.encuestas_api.auth.application.usecase;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.application.port.in.LoginUseCase;
import com.example.encuestas_api.auth.application.port.out.LoadCredentialPort;
import com.example.encuestas_api.auth.application.port.out.PasswordHasherPort;
import com.example.encuestas_api.auth.application.port.out.TokenEncoderPort;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.exception.UserNotFoundException;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.Email;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepo;
    private final LoadCredentialPort credLoader;
    private final PasswordHasherPort hasher;
    private final TokenEncoderPort tokenEncoder;

    public LoginService(UserRepositoryPort userRepo,
                        LoadCredentialPort credLoader,
                        PasswordHasherPort hasher,
                        TokenEncoderPort tokenEncoder) {
        this.userRepo = userRepo;
        this.credLoader = credLoader;
        this.hasher = hasher;
        this.tokenEncoder = tokenEncoder;
    }

    @Override
    public AuthResult handle(String email, String rawPassword) {
        User user = userRepo.findByEmail(Email.of(email))
                .orElseThrow(() -> UserNotFoundException.byEmail(email));

        String hash = credLoader.findPasswordHashByUserId(user.getId())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!hasher.matches(rawPassword, hash)) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        var roles = user.isSystemAdmin() ? List.of("ADMIN") : List.of("USER");
        String token = tokenEncoder.generateAccessToken(user.getEmail().getValue(), roles, user.getId());

        return new AuthResult("Bearer", token, tokenEncoder.accessTokenExpiresInSeconds(),
                user.getId(), user.getEmail().getValue(), user.getFullName().getValue(), user.isSystemAdmin());
    }
}
