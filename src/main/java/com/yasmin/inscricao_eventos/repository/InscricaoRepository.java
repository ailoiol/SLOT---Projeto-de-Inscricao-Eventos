package com.yasmin.inscricao_eventos.repository;

import com.yasmin.inscricao_eventos.model.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoRepository extends JpaRepository<Inscricao, Integer> {

    boolean existsByUsuarioIdAndEventoId(Integer usuarioId, Integer eventoId);

}