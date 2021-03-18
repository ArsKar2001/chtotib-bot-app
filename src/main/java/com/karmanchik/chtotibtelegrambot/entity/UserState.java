package com.karmanchik.chtotibtelegrambot.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_state")
@Setter
@Getter
@NoArgsConstructor
public class UserState {
    public static final Integer NONE = 200;
    public static final Integer SELECT_COURSE = 201;
    public static final Integer SELECT_GROUP = 202;
    public static final Integer SELECT_ROLE = 203;
    public static final Integer SELECT_OPTION = 204;
    public static final Integer ENTER_NAME = 205;
    public static final Integer SELECT_TEACHER = 206;



    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_user_state", unique = true)
    @NotNull
    private Integer id;

    @Column(name = "code_user_state", unique = true, nullable = false)
    @NotBlank
    private String code;

    @Column(name = "description_user_state")
    private String description;

    public UserState(@NotNull Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserState{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
