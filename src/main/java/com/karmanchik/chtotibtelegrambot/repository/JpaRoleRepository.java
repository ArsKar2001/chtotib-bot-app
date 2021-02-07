package com.karmanchik.chtotibtelegrambot.repository;

import com.karmanchik.chtotibtelegrambot.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;

public interface JpaRoleRepository extends JpaRepository<Role, Integer> {
    Role getByNameRole(@NotBlank String nameRole);
}
