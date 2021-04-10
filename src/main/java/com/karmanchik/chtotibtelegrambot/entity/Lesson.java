package com.karmanchik.chtotibtelegrambot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.karmanchik.chtotibtelegrambot.entity.enums.WeekType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "lessons")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lesson extends BaseEntity {
    @Column(name = "pair_number")
    @NotNull
    private Integer pairNumber;

    @Column(name = "day")
    @NotNull
    private Integer day;

    @Column(name = "discipline", nullable = false)
    private String discipline;

    @Column(name = "auditorium", nullable = false)
    private String auditorium;

    @Column(name = "week_type")
    @NotNull
    private WeekType weekType;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Group group;

    @JsonBackReference
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Teacher> teachers;

    @Override
    public String toString() {
        return "Lesson{" +
                "pairNumber=" + pairNumber +
                ", day=" + day +
                ", discipline='" + discipline + '\'' +
                ", auditorium='" + auditorium + '\'' +
                ", weekType=" + weekType +
                ", id=" + id +
                '}';
    }
}
