package com.example.encuestas_api.Controllers;


import com.example.encuestas_api.DTOS.LoginResponse;
import com.example.encuestas_api.DTOS.LoginUserDto;
import com.example.encuestas_api.DTOS.RegisterUserDto;
import com.example.encuestas_api.Models.User;
import com.example.encuestas_api.Services.AuthenticationService;
import com.example.encuestas_api.Services.JwtService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        //TODO: process POST request
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@RequestBody LoginUserDto loginUserDto) {
        //TODO: process POST request
        User authenticadeUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticadeUser);
        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }


}
