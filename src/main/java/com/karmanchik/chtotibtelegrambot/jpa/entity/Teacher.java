package com.karmanchik.chtotibtelegrambot.jpa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.karmanchik.chtotibtelegrambot.jpa.models.GroupOrTeacher;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "teacher")
@Getter
@Setter
@Builder(builderMethodName = "hiddenBuilder")
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends BaseEntity implements GroupOrTeacher {
    @Setter
    @Getter
    @Column(name = "name")
    @NotNull
    private String name;

    @Setter
    @JsonBackReference
    @OneToOne(mappedBy = "teacher")
    private User user;

    @Setter
    @JsonManagedReference
    @OneToMany(mappedBy = "teacher")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy("day, pairNumber ASC")
    private List<Lesson> lessons;

    @Setter
    @JsonManagedReference
    @OneToMany(mappedBy = "teacher")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy(value = "date, pairNumber ASC")
    private List<Replacement> replacements;

    public static TeacherBuilder builder(String name) {
        return hiddenBuilder().name(name);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", lessons=" + lessons +
                ", replacements=" + replacements +
                ", id=" + id +
                '}';
    }
}
