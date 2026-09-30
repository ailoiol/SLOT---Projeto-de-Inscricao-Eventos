package com.yasmin.inscricao_eventos.dto;

import com.yasmin.inscricao_eventos.model.Inscricao;
import java.util.List;

public class InscricaoCriacaoRequestDTO {
    private Inscricao inscricao;
    private List<RespostaCriterioResponseDTO> respostas;

    public Inscricao getInscricao() { return inscricao; }
    public void setInscricao(Inscricao inscricao) { this.inscricao = inscricao; }
    public List<RespostaCriterioResponseDTO> getRespostas() { return respostas; }
    public void setRespostas(List<RespostaCriterioResponseDTO> respostas) { this.respostas = respostas; }
}