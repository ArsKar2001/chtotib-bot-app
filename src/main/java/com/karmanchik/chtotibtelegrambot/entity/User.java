package com.karmanchik.chtotibtelegrambot.entity;

import com.karmanchik.chtotibtelegrambot.model.GroupNone;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "chat_id", name = "users_unique_chatid_idx")})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractBaseEntity {
    private static final Integer GROUP_NONE_ID = 100;

    @Column(name = "chat_id", unique = true, nullable = false)
    @NotNull
    private Integer chatId;

    @Column(name = "name")
    @NotBlank
    private String name = "";

    @Column(name = "bot_lat_message_id", nullable = false)
    @NotBlank
    private Integer botLastMessageId = 0;

    @Column(name = "bot_state_id", nullable = false)
    @NotBlank
    private Integer botStateId = State.Bot.START.getId();

    @Column(name = "user_state_id", nullable = false)
    @NotBlank
    private Integer userStateId = State.User.NONE.getId();

    @Column(name = "role_id", nullable = false)
    @NotBlank
    private Integer roleId = State.Role.NONE.getId();

    @Column(name = "group_id", nullable = false)
    @NotBlank
    private Integer groupId = GROUP_NONE_ID;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Group group = GroupNone.getInstance();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_state_id", referencedColumnName = "id_user_state", insertable = false, updatable = false)
    private UserState userState;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bot_state_id", referencedColumnName = "id_bot_state", insertable = false, updatable = false)
    private BotState botState;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Role role;

    public static class UserBuilder {

        private final Integer chatId;
        private String name;
        private Integer botLastMessageId;
        private Integer botStateId;
        private Integer userStateId;
        private Integer roleId;
        private Integer groupId;

        public UserBuilder(Integer chatId) {
            this.chatId = chatId;
        }

        public UserBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder setBotLastMessageId(Integer botLastMessageId) {
            this.botLastMessageId = botLastMessageId;
            return this;
        }

        public UserBuilder setBotStateId(Integer botStateId) {
            this.botStateId = botStateId;
            return this;
        }

        public UserBuilder setUserStateId(Integer userStateId) {
            this.userStateId = userStateId;
            return this;
        }

        public UserBuilder setRoleId(Integer roleId) {
            this.roleId = roleId;
            return this;
        }

        public UserBuilder setGroupId(Integer groupId) {
            this.groupId = groupId;
            return this;
        }

        public User build() {
            return new User(this);
        }

    }

    public User(int chatId) {
        this.chatId = chatId;
    }

    public User(UserBuilder builder) {
        this.chatId = builder.chatId;
        this.name = builder.name;
        this.botStateId = builder.botStateId;
        this.userStateId = builder.userStateId;
        this.roleId = builder.roleId;
        this.groupId = builder.groupId;
        this.botLastMessageId = builder.botLastMessageId;
    }
}
