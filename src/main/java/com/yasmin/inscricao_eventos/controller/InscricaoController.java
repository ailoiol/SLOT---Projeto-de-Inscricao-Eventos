package com.yasmin.inscricao_eventos.controller;

import com.yasmin.inscricao_eventos.dto.InscricaoResponseDTO;
import com.yasmin.inscricao_eventos.exception.RecursoNaoEncontradoException;
import com.yasmin.inscricao_eventos.exception.RegraDeNegocioException;
import com.yasmin.inscricao_eventos.model.Inscricao;
import com.yasmin.inscricao_eventos.repository.InscricaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inscricoes")
public class InscricaoController {

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @GetMapping
    public List<InscricaoResponseDTO> listarTodos() {
        return inscricaoRepository.findAll()
            .stream()
            .map(InscricaoResponseDTO::fromEntity)
            .toList();
    }

    @PostMapping
    public InscricaoResponseDTO criar(@RequestBody Inscricao inscricao) {
        boolean jaExiste = inscricaoRepository.existsByUsuarioIdAndEventoId(
            inscricao.getUsuario().getId(),
            inscricao.getEvento().getId()
        );

        if (jaExiste) {
            throw new RegraDeNegocioException("Este usuário já está inscrito neste evento");
        }

        Inscricao salva = inscricaoRepository.save(inscricao);
        return InscricaoResponseDTO.fromEntity(salva);
    }

    @PutMapping("/{id}")
    public InscricaoResponseDTO atualizar(@PathVariable Integer id, @RequestBody Inscricao dadosAtualizados) {
        Inscricao inscricao = inscricaoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Inscrição com id " + id + " não encontrada"));

        inscricao.setUsuario(dadosAtualizados.getUsuario());
        inscricao.setEvento(dadosAtualizados.getEvento());
        inscricao.setStatus(dadosAtualizados.getStatus());

        Inscricao atualizada = inscricaoRepository.save(inscricao);
        return InscricaoResponseDTO.fromEntity(atualizada);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Integer id) {
        inscricaoRepository.deleteById(id);
    }
}