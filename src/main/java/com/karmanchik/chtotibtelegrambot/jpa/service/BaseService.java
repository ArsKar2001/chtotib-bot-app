package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface BaseService<T extends BaseEntity> {
    <S extends T> S save(S s);

    <S extends T> List<S> saveAll(List<S> s);

    void deleteById(Integer id);

    <S extends T> void delete(S s);

    <S extends T> void deleteAll();

    <S extends T> Optional<S> findById(Integer id);

    <S extends T> List<S> findAll();
}
