package com.karmanchik.chtotibtelegrambot.jpa.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "replacement")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Replacement extends BaseEntity {

    @Getter
    @Setter
    @Column(name = "pair_number")
    @NotNull
    private String pairNumber;

    @Getter
    @Setter
    @Column(name = "discipline")
    @NotNull
    private String discipline;

    @Getter
    @Setter
    @Column(name = "auditorium")
    @NotNull
    private String auditorium;

    @Setter
    @Getter
    @Column(name = "group_name", nullable = false)
    private String groupName;

    @Setter
    @Getter
    @Column(name = "teacher_name", nullable = false)
    private String teacherName;

    @Getter
    @Setter
    @Column(name = "date_value")
    @NotNull
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

}
