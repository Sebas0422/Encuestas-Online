package com.example.encuestas_api.forms.domain.valueobject;

public final class ResponseLimitPolicy {
    public enum Mode { ONE_PER_USER, LIMITED_N, UNLIMITED }
    private final Mode mode;
    private final Integer maxResponsesPerUser;

    private ResponseLimitPolicy(Mode mode, Integer n) {
        if (mode == Mode.LIMITED_N && (n == null || n < 1))
            throw new IllegalArgumentException("n inválido para LIMITED_N");
        if (mode != Mode.LIMITED_N && n != null)
            throw new IllegalArgumentException("n solo aplica a LIMITED_N");
        this.mode = mode;
        this.maxResponsesPerUser = n;
    }
    public static ResponseLimitPolicy onePerUser(){ return new ResponseLimitPolicy(Mode.ONE_PER_USER, null); }
    public static ResponseLimitPolicy unlimited(){ return new ResponseLimitPolicy(Mode.UNLIMITED, null); }
    public static ResponseLimitPolicy limitedN(int n){ return new ResponseLimitPolicy(Mode.LIMITED_N, n); }

    public Mode mode(){ return mode; }
    public Integer maxResponsesPerUser(){ return maxResponsesPerUser; }
}
