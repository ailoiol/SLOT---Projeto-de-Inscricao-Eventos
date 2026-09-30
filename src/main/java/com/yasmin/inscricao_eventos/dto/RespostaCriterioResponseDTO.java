package com.yasmin.inscricao_eventos.dto;

import com.yasmin.inscricao_eventos.model.RespostaCriterio;

public class RespostaCriterioResponseDTO {

    private Integer id;
    private Integer inscricaoId;
    private Integer criterioPersonalizadoId;
    private String criterioCampo;
    private String valor;

    public RespostaCriterioResponseDTO(Integer id, Integer inscricaoId, Integer criterioPersonalizadoId,
                                        String criterioCampo, String valor) {
        this.id = id;
        this.inscricaoId = inscricaoId;
        this.criterioPersonalizadoId = criterioPersonalizadoId;
        this.criterioCampo = criterioCampo;
        this.valor = valor;
    }

    public static RespostaCriterioResponseDTO fromEntity(RespostaCriterio resposta) {
        return new RespostaCriterioResponseDTO(
            resposta.getId(),
            resposta.getInscricao().getId(),
            resposta.getCriterioPersonalizado().getId(),
            resposta.getCriterioPersonalizado().getCampo(),
            resposta.getValor()
        );
    }

    public Integer getId() { return id; }
    public Integer getInscricaoId() { return inscricaoId; }
    public Integer getCriterioPersonalizadoId() { return criterioPersonalizadoId; }
    public String getCriterioCampo() { return criterioCampo; }
    public String getValor() { return valor; }
}