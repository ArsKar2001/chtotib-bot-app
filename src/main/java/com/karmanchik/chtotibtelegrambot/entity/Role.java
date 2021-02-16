package com.karmanchik.chtotibtelegrambot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "role", uniqueConstraints = {@UniqueConstraint(name = "role_name_role_uindex", columnNames = "name_role")})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @NotNull
    private Integer id;

    @Column(name = "name_role")
    @NotBlank
    private String nameRole;

    @Column(name = "description")
    private String description;

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", nameRole='" + nameRole + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public enum Instance {
        NONE(100),
        TEACHER(101),
        STUDENT(102);

        private final int id;

        Instance(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}
