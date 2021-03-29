package com.karmanchik.chtotibtelegrambot.jpa.models;

public interface IdTeacherName extends BaseModel {
    @Override
    Integer getId();

    @Override
    String getName();
}
