package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.User;

import javax.validation.constraints.NotNull;

public interface UserService {
    User findByChatId(@NotNull Integer chatId);
    User save(User user);
}
