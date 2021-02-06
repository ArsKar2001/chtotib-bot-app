package com.karmanchik.chtotibtelegrambot.entity;

import com.karmanchik.chtotibtelegrambot.model.Role;
import com.karmanchik.chtotibtelegrambot.model.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "chat_id", name = "users_unique_chatid_idx")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractBaseEntity {
    private static final Integer GROUP_NONE_ID = 100;

    @Column(name = "chat_id", unique = true, nullable = false)
    @NotNull
    private Integer chatId;

    @Column(name = "name", nullable = false)
    @NotBlank
    private String name;

    @Column(name = "bot_state", nullable = false)
    @NotBlank
    private State botState;

    @Column(name = "bot_lat_message_id", nullable = false)
    @NotBlank
    private Integer botLastMessageId;

    @Column(name = "user_state", nullable = false)
    @NotBlank
    private State userState;

    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "role_name", nullable = false)
    @NotBlank
    private String roleName;

    public User(int chatId) {
        this.chatId = chatId;
        this.name = String.valueOf(chatId);
        this.botState = State.START;
        this.userState = State.NONE;
        this.roleName = Role.NONE.toString();
        this.groupId = GROUP_NONE_ID;
        this.botLastMessageId = chatId;
    }
}
