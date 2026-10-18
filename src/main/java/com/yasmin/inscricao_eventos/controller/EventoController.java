package com.yasmin.inscricao_eventos.controller;

import com.yasmin.inscricao_eventos.exception.RecursoNaoEncontradoException;
import com.yasmin.inscricao_eventos.exception.RegraDeNegocioException;
import com.yasmin.inscricao_eventos.dto.EventoResponseDTO;
import com.yasmin.inscricao_eventos.model.Evento;
import com.yasmin.inscricao_eventos.model.Usuario;
import com.yasmin.inscricao_eventos.repository.EventoRepository;
import com.yasmin.inscricao_eventos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public List<EventoResponseDTO> listarTodos() {
        return eventoRepository.findAll()
            .stream()
            .map(EventoResponseDTO::fromEntity)
            .toList();
    }

    @GetMapping(params = "organizadorId")
    public List<EventoResponseDTO> listarPorOrganizador(@RequestParam Integer organizadorId) {
        return eventoRepository.findByOrganizadorId(organizadorId)
            .stream()
            .map(EventoResponseDTO::fromEntity)
            .toList();
}

    @GetMapping("/{id}")
    public EventoResponseDTO buscarPorId(@PathVariable Integer id) {
        Evento evento = eventoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Evento com id " + id + " não encontrado"));
        return EventoResponseDTO.fromEntity(evento);
}

    @PostMapping
    public EventoResponseDTO criar(@Valid @RequestBody Evento evento) {
        Usuario organizador = usuarioRepository.findById(evento.getOrganizador().getId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Organizador não encontrado"));

        if (!"organizador".equals(organizador.getTipo())) {
            throw new RegraDeNegocioException("Apenas contas do tipo organizador podem criar eventos");
        }

        evento.setOrganizador(organizador);
        Evento salvo = eventoRepository.save(evento);
        return EventoResponseDTO.fromEntity(salvo);
    }

    @PutMapping("/{id}")
    public EventoResponseDTO atualizar(@PathVariable Integer id, @Valid @RequestBody Evento dadosAtualizados) {
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