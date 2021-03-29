package com.karmanchik.chtotibtelegrambot.jpa.service;

import com.karmanchik.chtotibtelegrambot.jpa.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface BaseService<T extends BaseEntity> {
    <S extends T> S save(S s);

    List<T> saveAll(List<T> t);

    void deleteById(Integer id);

    void delete(T t);

    void deleteAll();

    Optional<T> findById(Integer id);

    List<T> findAll();
}
