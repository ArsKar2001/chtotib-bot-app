package com.karmanchik.chtotibtelegrambot.repository;

import com.karmanchik.chtotibtelegrambot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface JpaUserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByChatId(@NotNull Integer chatId);
    boolean existsUserByChatId(@NotNull Integer chatId);
}
