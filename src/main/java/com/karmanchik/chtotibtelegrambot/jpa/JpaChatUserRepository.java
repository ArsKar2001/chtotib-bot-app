package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
@Transactional
public interface JpaChatUserRepository extends JpaRepository<ChatUser, Integer> {
    Optional<ChatUser> findByChatIdAndUserName(@NotNull Integer chatId, @NotNull String userName);
}
