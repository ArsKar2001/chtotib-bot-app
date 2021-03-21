package com.karmanchik.chtotibtelegrambot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "replacement")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Replacement extends AbstractBaseEntity {

    @Column(name = "lessons", columnDefinition = "json", nullable = false)
    @Type(type = "json")
    private String timetable;

    @Column(name = "date_value")
    @NotNull
    private LocalDate date;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "group_id")
    private Group group;

    @Override
    public String toString() {
        return "Replacement{" +
                "timetable='" + timetable + '\'' +
                ", date=" + date +
                ", group=" + group +
                ", id=" + id +
                '}';
    }
}
