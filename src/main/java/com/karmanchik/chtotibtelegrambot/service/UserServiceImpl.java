package com.karmanchik.chtotibtelegrambot.service;

import com.karmanchik.chtotibtelegrambot.entity.User;
import com.karmanchik.chtotibtelegrambot.repository.JpaUserRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Log4j
public class UserServiceImpl implements UserService {
    private final JpaUserRepository userRepository;

    public UserServiceImpl(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByChatId(@NotNull Integer chatId) {
        return userRepository.findByChatId(chatId).orElseGet(() -> userRepository.save(new User(chatId)));
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
