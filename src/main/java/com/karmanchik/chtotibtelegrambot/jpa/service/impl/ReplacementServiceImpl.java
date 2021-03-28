package com.karmanchik.chtotibtelegrambot.jpa.service.impl;

import com.karmanchik.chtotibtelegrambot.jpa.JpaReplacementRepository;
import com.karmanchik.chtotibtelegrambot.jpa.entity.Replacement;
import com.karmanchik.chtotibtelegrambot.jpa.service.ReplacementService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ReplacementServiceImpl implements ReplacementService {
    private final JpaReplacementRepository replacementRepository;

    public ReplacementServiceImpl(JpaReplacementRepository replacementRepository) {
        this.replacementRepository = replacementRepository;
    }

    @Override
    public <S extends Replacement> S save(S s) {
        return replacementRepository.save(s);
    }

    @Override
    public <S extends Replacement> List<S> saveAll(List<S> s) {
        return replacementRepository.saveAll(s);
    }

    @Override
    public void deleteById(Integer id) {
        replacementRepository.deleteById(id);
    }

    @Override
    public <S extends Replacement> void delete(S s) {
        replacementRepository.delete(s);
    }

    @Override
    public <S extends Replacement> void deleteAll() {
        replacementRepository.deleteAll();
    }

    @Override
    public <S extends Replacement> Optional<S> findById(Integer id) {
        return (Optional<S>) replacementRepository.findById(id);
    }

    @Override
    public <S extends Replacement> List<S> findAll() {
        return (List<S>) replacementRepository.findAll();
    }
}
