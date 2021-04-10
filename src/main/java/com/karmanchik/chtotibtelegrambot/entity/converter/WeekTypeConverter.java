package com.karmanchik.chtotibtelegrambot.entity.converter;

import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class WeekTypeConverter implements AttributeConverter<WeekType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WeekType weekType) {
        if (weekType == null) {
            return null;
        }
        return weekType.getCode();
    }

    @Override
    public WeekType convertToEntityAttribute(Integer code) {
        return code == null ? null : Stream.of(WeekType.values())
                .filter(weekType -> weekType.getCode() == code)
                .findFirst()
                .orElseThrow(IllegalAccessError::new);
    }
}
