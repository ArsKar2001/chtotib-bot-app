package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.entity.constants.Constants;
import com.karmanchik.chtotibtelegrambot.repository.JpaUserRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
@Log4j
public class UserService {
    public static final String STUDENT = "STUDENT";
    public static final String TEACHER = "TEACHER";

    private final JpaUserRepository userRepository;

    public UserService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByChatIdAndName(@NotNull Integer chatId, String userName) {
        return userRepository.findByChatIdAndName(chatId, userName)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                            .chatId(chatId)
                            .name(userName)
                            .roleId(Constants.Role.NONE)
                            .userStateId(Constants.User.NONE)
                            .botStateId(Constants.Bot.START)
                        .build()
                ));
    }

    public com.karmanchik.chtotibtelegrambot.entity.User save(com.karmanchik.chtotibtelegrambot.entity.User user) {
        return userRepository.save(user);
    }

    public boolean isStudent(com.karmanchik.chtotibtelegrambot.entity.User user) {
        return user.getRole().getNameRole().equalsIgnoreCase(STUDENT);
    }

    public boolean isTeacher(com.karmanchik.chtotibtelegrambot.entity.User user) {
        return user.getRole().getNameRole().equalsIgnoreCase(TEACHER);
    }
}
