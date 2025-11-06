package com.example.encuestas_api.auth.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    private String secret;
    private long accessMinutes = 60;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getAccessMinutes() { return accessMinutes; }
    public void setAccessMinutes(long accessMinutes) { this.accessMinutes = accessMinutes; }
}
