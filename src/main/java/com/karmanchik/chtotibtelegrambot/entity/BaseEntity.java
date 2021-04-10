package com.karmanchik.chtotibtelegrambot.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class BaseEntity {
    public static final int START_SEQ = 100000;

    @Id
    @Setter
    @Getter
    @SequenceGenerator(name = "global_seq", sequenceName = "global_seq", allocationSize = 1, initialValue = START_SEQ)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "global_seq")
    protected Integer id;
}


