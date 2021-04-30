package com.karmanchik.chtotibtelegrambot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.karmanchik.chtotibtelegrambot.model.GroupOrTeacher;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teacher")
@Getter
@Setter
@Builder(builderMethodName = "hiddenBuilder")
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends BaseEntity implements GroupOrTeacher {
    @Column(name = "name")
    @NotNull
    private String name;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<User> users;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "teachers",fetch = FetchType.LAZY)
    @OrderBy("day, pairNumber ASC")
    private Set<Lesson> lessons;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "teachers",fetch = FetchType.LAZY)
    @OrderBy("date, pairNumber ASC")
    private Set<Replacement> replacements;

    public static TeacherBuilder builder(String name) {
        return hiddenBuilder().name(name);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
