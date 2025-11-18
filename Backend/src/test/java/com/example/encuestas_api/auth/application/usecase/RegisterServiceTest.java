package com.example.encuestas_api.auth.application.usecase;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.application.port.out.PasswordHasherPort;
import com.example.encuestas_api.auth.application.port.out.SaveCredentialPort;
import com.example.encuestas_api.auth.application.port.out.TokenEncoderPort;
import com.example.encuestas_api.auth.domain.model.Credential;
import com.example.encuestas_api.users.application.port.in.CreateUserUseCase;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.exception.UserAlreadyExistsException;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterServiceTest {

    private UserRepositoryPort userRepo;
    private CreateUserUseCase createUserUC;
    private PasswordHasherPort hasher;
    private SaveCredentialPort credSaver;
    private TokenEncoderPort tokenEncoder;
    private Clock clock;
    private RegisterService registerService;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepositoryPort.class);
        createUserUC = mock(CreateUserUseCase.class);
        hasher = mock(PasswordHasherPort.class);
        credSaver = mock(SaveCredentialPort.class);
        tokenEncoder = mock(TokenEncoderPort.class);
        clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneId.of("UTC"));

        registerService = new RegisterService(userRepo, createUserUC, hasher, credSaver, tokenEncoder, clock);
    }

    @Test
    void shouldRegisterNewUserSuccessfully() {
        // given
        String email = "new@example.com";
        String fullName = "New User";
        String rawPassword = "password123";
        UUID userId = UUID.randomUUID();

        User user = mock(User.class);

        when(userRepo.existsByEmail(Email.of(email))).thenReturn(false);
        when(createUserUC.handle(email, fullName, false)).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(Email.of(email));
        when(user.getFullName()).thenReturn(() -> fullName);
        when(user.isSystemAdmin()).thenReturn(false);

        when(hasher.hash(rawPassword)).thenReturn("hashedPass");
        when(tokenEncoder.generateAccessToken(email, List.of("USER"), userId)).thenReturn("fakeToken");
        when(tokenEncoder.accessTokenExpiresInSeconds()).thenReturn(3600L);

        // when
        AuthResult result = registerService.handle(email, fullName, rawPassword, false);

        // then
        assertNotNull(result);
        assertEquals("Bearer", result.getTokenType());
        assertEquals("fakeToken", result.getAccessToken());
        verify(credSaver).save(any(Credential.class));
    }

    @Test
    void shouldThrowWhenUserAlreadyExists() {
        when(userRepo.existsByEmail(any())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> registerService.handle("exist@example.com", "Name", "pwd", false));
    }
}
