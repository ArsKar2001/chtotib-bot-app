package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.repository.JpaUserRepository;
import com.karmanchik.chtotibtelegrambot.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
@Log4j
public class UserService {
    private final JpaUserRepository userRepository;

    public UserService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByChatId(@NotNull Integer chatId) {
        return userRepository.findByChatId(chatId).orElseGet(() -> userRepository.save(new User(chatId)));
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
