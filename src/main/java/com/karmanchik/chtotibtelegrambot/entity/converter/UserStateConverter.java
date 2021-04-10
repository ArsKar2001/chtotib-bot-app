package com.karmanchik.chtotibtelegrambot.entity.converter;

import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class UserStateConverter implements AttributeConverter<UserState, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserState userState) {
        return userState == null ? null : userState.getCode();
    }

    @Override
    public UserState convertToEntityAttribute(Integer code) {
        return code == null ? null : Stream.of(UserState.values())
                .filter(userState -> userState.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }
}
