package com.yasmin.inscricao_eventos.repository;

import com.yasmin.inscricao_eventos.model.RespostaCriterio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespostaCriterioRepository extends JpaRepository<RespostaCriterio, Integer> {
    List<RespostaCriterio> findByInscricaoId(Integer inscricaoId);
}