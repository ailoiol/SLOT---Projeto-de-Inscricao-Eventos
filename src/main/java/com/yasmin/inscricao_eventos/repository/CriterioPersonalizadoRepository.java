package com.yasmin.inscricao_eventos.repository;

import com.yasmin.inscricao_eventos.model.CriterioPersonalizado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriterioPersonalizadoRepository extends JpaRepository<CriterioPersonalizado, Integer> {
    List<CriterioPersonalizado> findByEventoId(Integer eventoId);
}