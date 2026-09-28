package com.yasmin.inscricao_eventos.dto;

import com.yasmin.inscricao_eventos.model.Inscricao;
import java.time.LocalDateTime;

public class InscricaoResponseDTO {

    private Integer id;
    private String status;
    private LocalDateTime criadoEm;
    private UsuarioResponseDTO usuario;
    private EventoResponseDTO evento;

    public InscricaoResponseDTO(Integer id, String status, LocalDateTime criadoEm,
                                 UsuarioResponseDTO usuario, EventoResponseDTO evento) {
        this.id = id;
        this.status = status;
        this.criadoEm = criadoEm;
        this.usuario = usuario;
        this.evento = evento;
    }

    public static InscricaoResponseDTO fromEntity(Inscricao inscricao) {
    return new InscricaoResponseDTO(
        inscricao.getId(),
        inscricao.getStatus(),
        inscricao.getCriadoEm(),
        UsuarioResponseDTO.fromEntity(inscricao.getUsuario()),
        EventoResponseDTO.fromEntity(inscricao.getEvento())
    );
}

    public Integer getId() { return id; }
    public String getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public UsuarioResponseDTO getUsuario() { return usuario; }
    public EventoResponseDTO getEvento() { return evento; }
}