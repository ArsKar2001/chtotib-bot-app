package com.karmanchik.chtotibtelegrambot.entity.converter;


import com.karmanchik.chtotibtelegrambot.entity.enums.Role;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Role, Integer> {
    @Override
    public Integer convertToDatabaseColumn(Role role) {
        return role == null ? null : role.getCode();
    }

    @Override
    public Role convertToEntityAttribute(Integer code) {
        return code == null ? null : Stream.of(Role.values())
                .filter(role -> role.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }
}
