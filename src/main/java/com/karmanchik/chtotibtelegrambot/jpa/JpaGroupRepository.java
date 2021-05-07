package com.karmanchik.chtotibtelegrambot.jpa;

import com.karmanchik.chtotibtelegrambot.entity.ChatUser;
import com.karmanchik.chtotibtelegrambot.entity.Group;
import com.karmanchik.chtotibtelegrambot.model.IdGroupName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public interface JpaGroupRepository extends JpaRepository<Group, Integer> {
    Optional<Group> getByName(@NotNull String groupName);

    @Query(nativeQuery = true,
            value = "SELECT g.id, g.name FROM groups g")
    List<IdGroupName> getAllGroupName();

    @Query(nativeQuery = true,
            value = "SELECT g.id, g.name FROM groups g " +
                    "WHERE lower(g.name) LIKE '%' || :academicYearSuffix || '%' " +
                    "ORDER BY g.name")
    List<IdGroupName> findAllByYearSuffix(@Param("academicYearSuffix") String academicYearSuffix);

    @Query("SELECT g FROM Group g " +
            "WHERE :user member of g.chatUsers")
    Optional<Group> findByUsers(ChatUser chatUser);
}
