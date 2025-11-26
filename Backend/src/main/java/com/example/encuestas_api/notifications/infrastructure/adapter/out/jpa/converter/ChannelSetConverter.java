package com.example.encuestas_api.notifications.infrastructure.adapter.out.jpa.converter;

import com.example.encuestas_api.notifications.domain.valueobject.Channel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

@Converter(autoApply = false)
public class ChannelSetConverter implements AttributeConverter<Set<Channel>, String> {
    @Override
    public String convertToDatabaseColumn(Set<Channel> attribute) {
        if (attribute == null || attribute.isEmpty()) return "";
        return attribute.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    @Override
    public Set<Channel> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return EnumSet.noneOf(Channel.class);
        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Channel::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Channel.class)));
    }
}
