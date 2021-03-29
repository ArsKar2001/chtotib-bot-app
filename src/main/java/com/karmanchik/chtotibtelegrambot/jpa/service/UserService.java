package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.User;

public interface UserService extends BaseService<User> {
    User findByChatIdAndUserName(Integer chatId, String userName);
}
