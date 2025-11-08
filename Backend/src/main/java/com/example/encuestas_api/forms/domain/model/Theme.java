package com.example.encuestas_api.forms.domain.model;

public record Theme(String mode, String primaryColor) {
    public Theme {
        if (mode == null || mode.isBlank()) throw new IllegalArgumentException("mode requerido");
        if (!mode.equals("light") && !mode.equals("dark")) throw new IllegalArgumentException("mode inválido");
        if (primaryColor != null && primaryColor.length() > 20) throw new IllegalArgumentException("primaryColor muy largo");
    }
    public static Theme defaultLight(){ return new Theme("light","#3b82f6"); }
}
