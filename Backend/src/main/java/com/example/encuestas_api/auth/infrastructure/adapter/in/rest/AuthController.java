package com.example.encuestas_api.auth.infrastructure.adapter.in.rest;

import com.example.encuestas_api.auth.application.port.in.LoginUseCase;
import com.example.encuestas_api.auth.application.port.in.RegisterUseCase;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.AuthResponse;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.example.encuestas_api.auth.infrastructure.adapter.in.rest.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUseCase registerUC;
    private final LoginUseCase loginUC;

    public AuthController(RegisterUseCase registerUC, LoginUseCase loginUC) {
        this.registerUC = registerUC;
        this.loginUC = loginUC;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        var result = registerUC.handle(req.email(), req.fullName(), req.password(), Boolean.TRUE.equals(req.systemAdmin()));
        return ResponseEntity.ok(AuthRestMapper.toResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        var result = loginUC.handle(req.email(), req.password());
        return ResponseEntity.ok(AuthRestMapper.toResponse(result));
    }
}
