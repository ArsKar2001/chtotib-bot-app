package com.karmanchik.chtotibtelegrambot.jpa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.karmanchik.chtotibtelegrambot.jpa.models.GroupOrTeacher;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(
        name = "groups",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "name",
                        name = "schedule_group_name_uindex")
        })
@EqualsAndHashCode(callSuper = true)
@Getter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor
@NoArgsConstructor
public class Group extends BaseEntity implements GroupOrTeacher {
    @Setter
    @Column(name = "name", unique = true)
    @NotNull
    private String name;

    @Setter
    @JsonBackReference
    @OneToOne(mappedBy = "group")
    private User user;

    @Setter
    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy("day, pairNumber ASC")
    private List<Lesson> lessons;

    @Setter
    @JsonManagedReference
    @OneToMany(mappedBy = "group")
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy(value = "date ASC")
    private List<Replacement> replacements;

    public static GroupBuilder builder(String name) {
        return hiddenBuilder().name(name);
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", lessons=" + lessons +
                ", replacements=" + replacements +
                ", id=" + id +
                '}';
    }
}

