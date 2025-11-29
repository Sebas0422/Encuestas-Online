package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.converter;

import com.example.encuestas_api.notifications.domain.model.NotificationType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = false)
public class NotificationTypeSetConverter implements AttributeConverter<Set<NotificationType>, String> {
    @Override
    public String convertToDatabaseColumn(Set<NotificationType> attribute) {
        if (attribute == null || attribute.isEmpty()) return "";
        return attribute.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    @Override
    public Set<NotificationType> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return EnumSet.noneOf(NotificationType.class);
        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(NotificationType::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(NotificationType.class)));
    }
}
