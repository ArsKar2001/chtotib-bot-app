package com.karmanchik.chtotibtelegrambot.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(
        name = "groups",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = "group_name",
                        name = "schedule_group_name_uindex")
        })
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group extends AbstractBaseEntity {
    @Setter
    @Getter
    @Column(name = "group_name", unique = true)
    @NotNull
    private String groupName;

    @Getter
    @Setter
    @Column(name = "timetable", columnDefinition = "json", nullable = false)
    @Type(type = "json")
    private String lessons;

    @Getter
    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(referencedColumnName = "id", columnDefinition = "group_id", updatable = false, nullable = false)
    private List<Replacement> replacements;

    public Group(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupName='" + groupName + '\'' +
                ", lessons='" + lessons + '\'' +
                ", replacements=" + replacements +
                ", id=" + id +
                '}';
    }
}
