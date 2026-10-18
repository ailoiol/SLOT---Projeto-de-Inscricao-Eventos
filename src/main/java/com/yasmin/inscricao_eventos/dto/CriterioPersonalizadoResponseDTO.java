package com.yasmin.inscricao_eventos.dto;

import com.yasmin.inscricao_eventos.model.CriterioPersonalizado;

public class CriterioPersonalizadoResponseDTO {

    private Integer id;
    private Integer eventoId;
    private String campo;
    private boolean obrigatorio;

    public CriterioPersonalizadoResponseDTO(Integer id, Integer eventoId, String campo, boolean obrigatorio) {
        this.id = id;
        this.eventoId = eventoId;
        this.campo = campo;
        this.obrigatorio = obrigatorio;
    }

    public static CriterioPersonalizadoResponseDTO fromEntity(CriterioPersonalizado criterio) {
        return new CriterioPersonalizadoResponseDTO(
            criterio.getId(),
            criterio.getEvento().getId(),
            criterio.getCampo(),
            Boolean.TRUE.equals(criterio.getObrigatorio())
        );
    }

    public Integer getId() { return id; }
    public Integer getEventoId() { return eventoId; }
    public String getCampo() { return campo; }
    public boolean isObrigatorio() { return obrigatorio; }
}