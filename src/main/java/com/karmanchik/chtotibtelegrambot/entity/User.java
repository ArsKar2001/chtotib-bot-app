package com.karmanchik.chtotibtelegrambot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.karmanchik.chtotibtelegrambot.entity.enums.BotState;
import com.karmanchik.chtotibtelegrambot.entity.enums.Role;
import com.karmanchik.chtotibtelegrambot.entity.enums.UserState;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "users")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Column(name = "chat_id", unique = true)
    @NotNull
    private Integer chatId;

    @Column(name = "user_name", unique = true)
    @NotNull
    private String userName;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_teacher",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "teacher_id", referencedColumnName = "id", nullable = false)})
    private Teacher teacher = null;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "user_group",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)})
    private Group group = null;


    @Column(name = "role_id")
    @NotNull
    private Role role = Role.NONE;

    @Column(name = "user_state_id")
    @NotNull
    private UserState userState = UserState.NONE;

    @Column(name = "bot_state_id")
    @NotNull
    private BotState botState = BotState.START;

    public static UserBuilder builder(Integer chatId, String userName) {
        return new UserBuilder().chatId(chatId).userName(userName);
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", userName='" + userName + '\'' +
                ", role=" + role +
                ", userState=" + userState +
                ", botState=" + botState +
                ", id=" + id +
                '}';
    }
}

