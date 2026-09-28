package com.yasmin.inscricao_eventos.repository;

import com.yasmin.inscricao_eventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
}