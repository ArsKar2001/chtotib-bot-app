package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.jpa.entity.Group;
import com.karmanchik.chtotibtelegrambot.jpa.models.IdGroupName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface JpaGroupRepository extends JpaRepository<Group, Integer> {
    Optional<Group> getByName(@NotNull String groupName);

    @Query("SELECT g.name FROM Group g")
    Optional<List<String>> getAllGroupName();

    @Query(nativeQuery = true,
    value = "SELECT g.id, g.name FROM groups g " +
            "WHERE g.name LIKE '%' || ?1 || '%' " +
            "ORDER BY g.name ASC ")
    List<IdGroupName> getAllGroupNameByYearSuffix(@Param("suffix") @NotNull String suffix);

    boolean existsById(@NotNull @Nullable Integer id);
}
