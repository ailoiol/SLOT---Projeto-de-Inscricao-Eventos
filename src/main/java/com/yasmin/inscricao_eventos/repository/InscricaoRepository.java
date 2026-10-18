package com.yasmin.inscricao_eventos.repository;

import com.yasmin.inscricao_eventos.model.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InscricaoRepository extends JpaRepository<Inscricao, Integer> {

    boolean existsByCpfParticipanteAndEventoId(String cpfParticipante, Integer eventoId);

    List<Inscricao> findByUsuarioId(Integer usuarioId);

    List<Inscricao> findByEventoId(Integer eventoId);

}