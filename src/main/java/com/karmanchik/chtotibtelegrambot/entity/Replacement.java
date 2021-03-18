package com.karmanchik.chtotibtelegrambot.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "replacement")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Replacement extends AbstractBaseEntity {

    @Column(name = "group_id")
    @NotNull
    private Integer groupId;

    @Column(name = "timetable", columnDefinition = "json", nullable = false)
    @Type(type = "json")
    private String timetable;

    @Column(name = "date_value")
    @NotNull
    private LocalDate date;


    @Override
    public String toString() {
        return "Replacement{" +
                "groupId=" + groupId +
                ", timetable='" + timetable + '\'' +
                ", date=" + date +
                ", id=" + id +
                '}';
    }
}
