package com.example.encuestas_api.questions.domain.model;

public final class TextSettings {
    private final TextMode mode;
    private final String placeholder;
    private final Integer minLength;
    private final Integer maxLength;

    private TextSettings(TextMode mode, String placeholder, Integer minLength, Integer maxLength) {
        if (mode == null) throw new IllegalArgumentException("TextMode requerido");
        if (minLength != null && minLength < 0) throw new IllegalArgumentException("minLength inválido");
        if (maxLength != null && maxLength < 1) throw new IllegalArgumentException("maxLength inválido");
        if (minLength != null && maxLength != null && minLength > maxLength)
            throw new IllegalArgumentException("minLength > maxLength");
        this.mode = mode;
        this.placeholder = placeholder == null ? null : placeholder.trim();
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public static TextSettings of(TextMode mode, String placeholder, Integer minLength, Integer maxLength){
        return new TextSettings(mode, placeholder, minLength, maxLength);
    }

    public TextMode mode(){ return mode; }
    public String placeholder(){ return placeholder; }
    public Integer minLength(){ return minLength; }
    public Integer maxLength(){ return maxLength; }
}
