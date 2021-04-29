package com.karmanchik.chtotibtelegrambot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.karmanchik.chtotibtelegrambot.entity.models.GroupOrTeacher;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

    @JsonBackReference
    @OneToOne(mappedBy = "teacher")
    private User user;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "teachers",fetch = FetchType.LAZY)
    @OrderBy("day, pairNumber ASC")
    @MapsId("teachersId")
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
