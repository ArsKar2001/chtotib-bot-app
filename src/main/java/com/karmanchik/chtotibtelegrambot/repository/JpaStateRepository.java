package com.karmanchik.chtotibtelegrambot.repository;

import com.karmanchik.chtotibtelegrambot.entity.BotState;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;

public interface JpaStateRepository extends JpaRepository<BotState, Integer> {
    BotState getByCode(@NotBlank String code);
}
