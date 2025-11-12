package com.example.encuestas_api.auth.application.usecase;

import com.example.encuestas_api.auth.application.dto.AuthResult;
import com.example.encuestas_api.auth.application.port.out.LoadCredentialPort;
import com.example.encuestas_api.auth.application.port.out.PasswordHasherPort;
import com.example.encuestas_api.auth.application.port.out.TokenEncoderPort;
import com.example.encuestas_api.users.application.port.out.UserRepositoryPort;
import com.example.encuestas_api.users.domain.exception.UserNotFoundException;
import com.example.encuestas_api.users.domain.model.User;
import com.example.encuestas_api.users.domain.valueobject.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private UserRepositoryPort userRepo;
    private LoadCredentialPort credLoader;
    private PasswordHasherPort hasher;
    private TokenEncoderPort tokenEncoder;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepositoryPort.class);
        credLoader = mock(LoadCredentialPort.class);
        hasher = mock(PasswordHasherPort.class);
        tokenEncoder = mock(TokenEncoderPort.class);
        loginService = new LoginService(userRepo, credLoader, hasher, tokenEncoder);
    }

    @Test
    void shouldReturnAuthResultWhenCredentialsAreValid() {
        // given
        String email = "user@example.com";
        String password = "password123";
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        when(userRepo.findByEmail(Email.of(email))).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(userId);
        when(credLoader.findPasswordHashByUserId(userId)).thenReturn(Optional.of("hashedPass"));
        when(hasher.matches(password, "hashedPass")).thenReturn(true);
        when(user.isSystemAdmin()).thenReturn(false);
        when(user.getEmail()).thenReturn(Email.of(email));
        when(user.getFullName()).thenReturn(() -> "Test User");
        when(tokenEncoder.generateAccessToken(email, List.of("USER"), userId)).thenReturn("fakeToken");
        when(tokenEncoder.accessTokenExpiresInSeconds()).thenReturn(3600L);

        // when
        AuthResult result = loginService.handle(email, password);

        // then
        assertNotNull(result);
        assertEquals("Bearer", result.getTokenType());
        assertEquals("fakeToken", result.getAccessToken());
        assertEquals(email, result.getEmail());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepo.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> loginService.handle("missing@example.com", "pwd"));
    }

    @Test
    void shouldThrowWhenPasswordDoesNotMatch() {
        String email = "user@example.com";
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(userId);
        when(credLoader.findPasswordHashByUserId(userId)).thenReturn(Optional.of("hash"));
        when(hasher.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> loginService.handle(email, "wrongpwd"));
    }

    @Test
    void shouldThrowWhenCredentialNotFound() {
        String email = "user@example.com";
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);

        when(userRepo.findByEmail(any())).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(userId);
        when(credLoader.findPasswordHashByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> loginService.handle(email, "pwd"));
    }
}
