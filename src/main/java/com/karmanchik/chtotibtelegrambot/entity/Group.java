package com.karmanchik.chtotibtelegrambot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.karmanchik.chtotibtelegrambot.entity.models.GroupOrTeacher;
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
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Builder(builderMethodName = "hiddenBuilder")
@AllArgsConstructor
@NoArgsConstructor
public class Group extends BaseEntity implements GroupOrTeacher {
    @Column(name = "name", unique = true)
    @NotNull
    private String name;

    @JsonBackReference
    @OneToOne(mappedBy = "group")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    @OrderBy("day, pairNumber ASC")
    private List<Lesson> lessons;

    @JsonManagedReference
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    @OrderBy(value = "date, pairNumber ASC")
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

