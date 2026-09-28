package com.yasmin.inscricao_eventos.controller;

import com.yasmin.inscricao_eventos.exception.RecursoNaoEncontradoException;
import com.yasmin.inscricao_eventos.dto.EventoResponseDTO;
import com.yasmin.inscricao_eventos.model.Evento;
import com.yasmin.inscricao_eventos.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @GetMapping
    public List<EventoResponseDTO> listarTodos() {
        return eventoRepository.findAll()
            .stream()
            .map(EventoResponseDTO::fromEntity)
            .toList();
}

    @PostMapping
    public EventoResponseDTO criar(@RequestBody Evento evento) {
        Evento salvo = eventoRepository.save(evento);
        return EventoResponseDTO.fromEntity(salvo);
    }


    @PutMapping("/{id}")
    public EventoResponseDTO atualizar(@PathVariable Integer id, @RequestBody Evento dadosAtualizados) {
    Evento evento = eventoRepository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Evento com id " + id + " não encontrado"));

        evento.setOrganizador(dadosAtualizados.getOrganizador());
        evento.setTitulo(dadosAtualizados.getTitulo());
        evento.setDescricao(dadosAtualizados.getDescricao());
        evento.setDataEvento(dadosAtualizados.getDataEvento());
        evento.setVagasTotais(dadosAtualizados.getVagasTotais());
        evento.setVagasOcupadas(dadosAtualizados.getVagasOcupadas());
        evento.setStatus(dadosAtualizados.getStatus());

        Evento atualizado = eventoRepository.save(evento);
        return EventoResponseDTO.fromEntity(atualizado);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Integer id) {
        eventoRepository.deleteById(id);
    }
}