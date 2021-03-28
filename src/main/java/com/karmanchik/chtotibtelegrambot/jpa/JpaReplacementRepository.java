package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Replacement;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface JpaReplacementRepository extends JpaRepository<Replacement, Integer> {
    Optional<Replacement> findByGroupId(@NotNull Integer groupId);

    @Transactional
    Optional<Replacement> findByGroupAndDate(Group group, @NotNull LocalDate date);

    @Transactional
    List<Replacement> findByGroup(Group group, Sort sort);
}
