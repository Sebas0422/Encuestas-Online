package com.example.encuestas_api.auth.application.usecase;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.application.port.in.RegisterUseCase;
import com.example.encuestas_api.auth.application.port.out.PasswordHasherPort;
import com.example.encuestas_api.auth.application.port.out.SaveCredentialPort;
import com.example.encuestas_api.auth.application.port.out.TokenEncoderPort;
import com.example.encuestas_api.auth.domain.model.Credential;
import com.example.encuestas_api.users.application.port.in.CreateUserUseCase;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.exception.UserAlreadyExistsException;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class RegisterService implements RegisterUseCase {

    private final UserRepositoryPort userRepo;
    private final CreateUserUseCase createUserUC;
    private final PasswordHasherPort hasher;
    private final SaveCredentialPort credSaver;
    private final TokenEncoderPort tokenEncoder;
    private final Clock clock;

    public RegisterService(UserRepositoryPort userRepo,
                           CreateUserUseCase createUserUC,
                           PasswordHasherPort hasher,
                           SaveCredentialPort credSaver,
                           TokenEncoderPort tokenEncoder,
                           Clock clock) {
        this.userRepo = userRepo;
        this.createUserUC = createUserUC;
        this.hasher = hasher;
        this.credSaver = credSaver;
        this.tokenEncoder = tokenEncoder;
        this.clock = clock;
    }

    @Override
    public AuthResult handle(String email, String fullName, String rawPassword, boolean systemAdmin) {
        var emailVO = Email.of(email);
        if (userRepo.existsByEmail(emailVO)) {
            throw new UserAlreadyExistsException(emailVO.getValue());
        }

        User user = createUserUC.handle(emailVO.getValue(), fullName, systemAdmin);

        String hash = hasher.hash(rawPassword);
        var now = Instant.now(clock);
        credSaver.save(Credential.createNew(user.getId(), hash, now));

        var roles = systemAdmin ? List.of("ADMIN") : List.of("USER");
        String token = tokenEncoder.generateAccessToken(user.getEmail().getValue(), roles, user.getId());

        return new AuthResult("Bearer", token, tokenEncoder.accessTokenExpiresInSeconds(),
                user.getId(), user.getEmail().getValue(), user.getFullName().getValue(), user.isSystemAdmin());
    }
}
