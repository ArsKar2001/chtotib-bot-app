package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
@Transactional
public interface JpaUserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByChatIdAndUserName(@NotNull Integer chatId, @NotNull String userName);
}
