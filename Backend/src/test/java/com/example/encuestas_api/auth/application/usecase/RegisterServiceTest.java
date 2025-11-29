package com.example.encuestas_api.auth.application.usecase;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.application.port.out.PasswordHasherPort;
import com.example.encuestas_api.auth.application.port.out.SaveCredentialPort;
import com.example.encuestas_api.auth.application.port.out.TokenEncoderPort;
import com.example.encuestas_api.users.application.port.in.CreateUserUseCase;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.exception.UserAlreadyExistsException;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.Email;
import com.example.encuestas_api.users.domain.valueobject.FullName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

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
        clock = Clock.fixed(Instant.parse("2023-01-01T00:00:00Z"), ZoneId.systemDefault());
        registerService = new RegisterService(userRepo, createUserUC, hasher, credSaver, tokenEncoder, clock);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        // given
        String email = "newuser@example.com";
        String fullName = "New User";
        String password = "password123";
        boolean systemAdmin = false;

        Long userId = 1L;
        User user = mock(User.class);

        when(userRepo.existsByEmail(Email.of(email))).thenReturn(false);
        when(createUserUC.handle(email, fullName, systemAdmin)).thenReturn(user);
        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn(Email.of(email));
        when(user.getFullName()).thenReturn(FullName.of(fullName));
        when(user.isSystemAdmin()).thenReturn(systemAdmin);
        when(hasher.hash(password)).thenReturn("hashedPassword");
        when(tokenEncoder.generateAccessToken(email, List.of("USER"), userId)).thenReturn("fakeToken");
        when(tokenEncoder.accessTokenExpiresInSeconds()).thenReturn(3600L);

        // when
        AuthResult result = registerService.handle(email, fullName, password, systemAdmin);

        // then
        assertNotNull(result);
        assertEquals("Bearer", result.tokenType());
        assertEquals("fakeToken", result.accessToken());
        assertEquals(email, result.email());
        assertEquals(fullName, result.fullName());
        assertFalse(result.systemAdmin());
        verify(credSaver, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenUserAlreadyExists() {
        String email = "existing@example.com";
        when(userRepo.existsByEmail(Email.of(email))).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> registerService.handle(email, "User", "pwd", false));

        verify(createUserUC, never()).handle(anyString(), anyString(), anyBoolean());
    }
}
