package com.example.encuestas_api.responses.domain.valueobject;

import java.util.Objects;

public final class Respondent {

    public enum Type { ANONYMOUS, USER, EMAIL, CODE }

    private final Type type;
    private final Long userId;
    private final String email;
    private final String code;

    private Respondent(Type type, Long userId, String email, String code) {
        this.type = Objects.requireNonNull(type);
        this.userId = userId;
        this.email = email;
        this.code = code;

        switch (type) {
            case ANONYMOUS -> {}
            case USER  -> { if (userId == null) throw new IllegalArgumentException("userId requerido"); }
            case EMAIL -> { if (email == null || email.isBlank()) throw new IllegalArgumentException("email requerido"); }
            case CODE  -> { if (code == null || code.isBlank()) throw new IllegalArgumentException("code requerido"); }
        }
    }

    public static Respondent anonymous()        { return new Respondent(Type.ANONYMOUS, null, null, null); }
    public static Respondent user(Long userId)  { return new Respondent(Type.USER, userId, null, null); }
    public static Respondent email(String email){ return new Respondent(Type.EMAIL, null, email, null); }
    public static Respondent code(String code)  { return new Respondent(Type.CODE, null, null, code); }

    public Type getType() { return type; }
    public Long getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getCode() { return code; }

    public boolean isAnonymous() { return type == Type.ANONYMOUS; }
}
