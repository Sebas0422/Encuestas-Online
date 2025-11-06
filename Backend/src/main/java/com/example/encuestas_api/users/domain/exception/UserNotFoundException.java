package com.example.encuestas_api.users.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("Usuario id=" + id + " no encontrado");
    }
    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("Usuario con email '" + email + "' no encontrado");
    }
    private UserNotFoundException(String message) { super(message); }
}
