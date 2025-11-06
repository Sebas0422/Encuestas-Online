package com.example.encuestas_api.campaigns.application.port.out;

public interface CheckUserExistsPort {
    boolean existsUserById(Long userId);
}
