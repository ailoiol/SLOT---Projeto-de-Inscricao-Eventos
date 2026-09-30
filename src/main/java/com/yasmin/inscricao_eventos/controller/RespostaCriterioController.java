package com.yasmin.inscricao_eventos.controller;

import com.yasmin.inscricao_eventos.model.RespostaCriterio;
import com.yasmin.inscricao_eventos.repository.RespostaCriterioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/respostas-criterio")
public class RespostaCriterioController {

    @Autowired
    private RespostaCriterioRepository respostaCriterioRepository;

    @GetMapping("/inscricao/{inscricaoId}")
    public List<RespostaCriterio> listarPorInscricao(@PathVariable Integer inscricaoId) {
        return respostaCriterioRepository.findByInscricaoId(inscricaoId);
    }

    @PostMapping
    public RespostaCriterio criar(@RequestBody RespostaCriterio resposta) {
        return respostaCriterioRepository.save(resposta);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Integer id) {
        respostaCriterioRepository.deleteById(id);
    }
}