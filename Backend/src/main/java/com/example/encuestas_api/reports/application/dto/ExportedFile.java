package com.example.encuestas_api.reports.application.dto;

public record ExportedFile(
        String filename,
        String contentType,
        byte[] bytes
) { }
