package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.User;

public interface UserService {
    User save(User user);

    boolean isStudent(User user);

    boolean isTeacher(User user);
}
