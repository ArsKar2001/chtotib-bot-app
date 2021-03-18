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
