package com.karmanchik.chtotibtelegrambot.entity;

import com.karmanchik.chtotibtelegrambot.model.InstanceRole;
import com.karmanchik.chtotibtelegrambot.model.InstanceState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import static com.karmanchik.chtotibtelegrambot.model.InstanceState.START;


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

    @Column(name = "bot_lat_message_id", nullable = false)
    @NotBlank
    private Integer botLastMessageId;

    @Column(name = "bot_state_id", nullable = false)
    @NotBlank
    private Integer botStateId;

    @Column(name = "user_state_id", nullable = false)
    @NotBlank
    private Integer userStateId;

    @Column(name = "role_id", nullable = false)
    @NotBlank
    private Integer roleId;

    @Column(name = "group_id", nullable = false)
    @NotBlank
    private Integer groupId;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Group group;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_state_id", referencedColumnName = "id_user_state", insertable = false, updatable = false)
    private UserState userState;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bot_state_id", referencedColumnName = "id_bot_state", insertable = false, updatable = false)
    private BotState botState;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Role role;

    public User(int chatId) {
        this.chatId = chatId;
        this.name = String.valueOf(chatId);
        this.botStateId = START.getId();
        this.userStateId = InstanceState.NONE.getId();
        this.roleId = InstanceRole.NONE.getId();
        this.groupId = GROUP_NONE_ID;
        this.botLastMessageId = 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", name='" + name + '\'' +
                ", botLastMessageId=" + botLastMessageId +
                ", botStateId=" + botStateId +
                ", userStateId=" + userStateId +
                ", roleId=" + roleId +
                ", groupId=" + groupId +
                ", group=" + group +
                ", userState=" + userState +
                ", botState=" + botState +
                ", role=" + role +
                ", id=" + id +
                '}';
    }
}
