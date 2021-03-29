package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Teacher;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdTeacherName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface JpaTeacherRepository extends JpaRepository<Teacher, Integer> {
    Optional<Teacher> getByName(@NotNull String name);

    @Query(nativeQuery = true,
            value = "SELECT t.id, t.name FROM teacher t")
    List<IdTeacherName> getAllNames();

    @Query(nativeQuery = true,
            value = "SELECT t.id, t.name FROM teacher t " +
                    "WHERE t.name LIKE '%' || ?1 || '%'" +
                    "ORDER BY t.name")
    List<IdTeacherName> getAllIdTeacherNameByName(String message);
}
