package com.yasmin.inscricao_eventos.repository;

import com.yasmin.inscricao_eventos.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import com.yasmin.inscricao_eventos.model.Evento;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    List<Evento> findByOrganizadorId(Integer organizadorId);
}