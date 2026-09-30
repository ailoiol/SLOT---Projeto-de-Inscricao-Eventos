package com.yasmin.inscricao_eventos.controller;

import com.yasmin.inscricao_eventos.dto.InscricaoResponseDTO;
import com.yasmin.inscricao_eventos.exception.RecursoNaoEncontradoException;
import com.yasmin.inscricao_eventos.exception.RegraDeNegocioException;
import com.yasmin.inscricao_eventos.model.Evento;
import com.yasmin.inscricao_eventos.model.Inscricao;
import com.yasmin.inscricao_eventos.model.Usuario;
import com.yasmin.inscricao_eventos.repository.EventoRepository;
import com.yasmin.inscricao_eventos.repository.InscricaoRepository;
import com.yasmin.inscricao_eventos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/inscricoes")
public class InscricaoController {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    public List<InscricaoResponseDTO> listarTodos() {
        return inscricaoRepository.findAll()
            .stream()
            .map(InscricaoResponseDTO::fromEntity)
            .toList();
    }

    @PostMapping
    public InscricaoResponseDTO criar(@RequestBody Inscricao inscricao) {
        Usuario responsavel = usuarioRepository.findById(inscricao.getResponsavel().getId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário responsável não encontrado"));

        if ("organizador".equals(responsavel.getTipo())) {
            throw new RegraDeNegocioException("Contas de organizador não podem se inscrever em eventos");
        }

        Evento evento = eventoRepository.findById(inscricao.getEvento().getId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Evento não encontrado"));

        if (!"aberto".equals(evento.getStatus())) {
            throw new RegraDeNegocioException("Este evento não está aberto para inscrições");
        }

        if (evento.getVagasOcupadas() >= evento.getVagasTotais()) {
            evento.setStatus("fechado");
            eventoRepository.save(evento);
            throw new RegraDeNegocioException("As vagas deste evento já se esgotaram");
        }

        boolean jaExiste = inscricaoRepository.existsByCpfParticipanteAndEventoId(
            inscricao.getCpfParticipante(),
            evento.getId()
        );

        if (jaExiste) {
            throw new RegraDeNegocioException("Este CPF já está inscrito neste evento");
        }

        inscricao.setResponsavel(responsavel);
        inscricao.setEvento(evento);
        Inscricao salva = inscricaoRepository.save(inscricao);

        evento.setVagasOcupadas(evento.getVagasOcupadas() + 1);
        if (evento.getVagasOcupadas() >= evento.getVagasTotais()) {
            evento.setStatus("fechado");
        }
        eventoRepository.save(evento);

        return InscricaoResponseDTO.fromEntity(salva);
    }

    @PutMapping("/{id}")
    public InscricaoResponseDTO atualizar(@PathVariable Integer id, @RequestBody Inscricao dadosAtualizados) {
        Inscricao inscricao = inscricaoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição com id " + id + " não encontrada"));

        inscricao.setResponsavel(dadosAtualizados.getResponsavel());
        inscricao.setEvento(dadosAtualizados.getEvento());
        inscricao.setStatus(dadosAtualizados.getStatus());

        Inscricao atualizada = inscricaoRepository.save(inscricao);
        return InscricaoResponseDTO.fromEntity(atualizada);
    }

    @DeleteMapping("/{id}")
    public InscricaoResponseDTO cancelar(@PathVariable Integer id) {
        Inscricao inscricao = inscricaoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição com id " + id + " não encontrada"));

        if ("cancelada".equals(inscricao.getStatus())) {
            throw new RegraDeNegocioException("Esta inscrição já está cancelada");
        }

        if (LocalDateTime.now().isAfter(inscricao.getEvento().getDataEvento().minusHours(2))) {
            throw new RegraDeNegocioException("Cancelamento só é permitido até 2h antes do evento");
        }

        inscricao.setStatus("cancelada");
        Inscricao cancelada = inscricaoRepository.save(inscricao);

        Evento evento = inscricao.getEvento();
        evento.setVagasOcupadas(Math.max(0, evento.getVagasOcupadas() - 1));
        if (!"cancelado".equals(evento.getStatus())) {
            evento.setStatus("aberto");
        }
        eventoRepository.save(evento);

        return InscricaoResponseDTO.fromEntity(cancelada);
    }
}