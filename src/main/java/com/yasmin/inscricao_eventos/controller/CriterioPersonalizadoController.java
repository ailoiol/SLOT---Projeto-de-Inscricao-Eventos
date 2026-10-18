package com.yasmin.inscricao_eventos.controller;

import com.yasmin.inscricao_eventos.exception.RecursoNaoEncontradoException;
import com.yasmin.inscricao_eventos.model.CriterioPersonalizado;
import com.yasmin.inscricao_eventos.repository.CriterioPersonalizadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.yasmin.inscricao_eventos.dto.CriterioPersonalizadoResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/criterios-personalizados")
public class CriterioPersonalizadoController {

    @Autowired
    private CriterioPersonalizadoRepository criterioPersonalizadoRepository;

    @GetMapping
    public List<CriterioPersonalizadoResponseDTO> listarTodos() {
        return criterioPersonalizadoRepository.findAll()
            .stream()
            .map(CriterioPersonalizadoResponseDTO::fromEntity)
            .toList();
}

    @GetMapping(params = "eventoId")
    public List<CriterioPersonalizadoResponseDTO> listarPorEvento(@RequestParam Integer eventoId) {
        return criterioPersonalizadoRepository.findByEventoId(eventoId)
            .stream()
            .map(CriterioPersonalizadoResponseDTO::fromEntity)
            .toList();
}

    @PostMapping
    public CriterioPersonalizado criar(@RequestBody CriterioPersonalizado criterioPersonalizado) {
        return criterioPersonalizadoRepository.save(criterioPersonalizado);
    }

    @PutMapping("/{id}")
    public CriterioPersonalizado atualizar(@PathVariable Integer id, @RequestBody CriterioPersonalizado dadosAtualizados) {
        CriterioPersonalizado criterioPersonalizado = criterioPersonalizadoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Critério personalizado com id " + id + " não encontrado"));

        criterioPersonalizado.setEvento(dadosAtualizados.getEvento());
        criterioPersonalizado.setCampo(dadosAtualizados.getCampo());
        criterioPersonalizado.setObrigatorio(dadosAtualizados.getObrigatorio());

        return criterioPersonalizadoRepository.save(criterioPersonalizado);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Integer id) {
        criterioPersonalizadoRepository.deleteById(id);
    }
}