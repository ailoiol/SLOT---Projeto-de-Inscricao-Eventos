package com.yasmin.inscricao_eventos.dto;

import com.yasmin.inscricao_eventos.model.Inscricao;
import java.time.LocalDateTime;


public class InscricaoResponseDTO {

    private Integer id;
    private String status;
    private LocalDateTime criadoEm;
    private UsuarioResponseDTO usuario;
    private EventoResponseDTO evento;
    private String nomeParticipante;
    private String cpfParticipante;
    private String telefoneParticipante;

    public InscricaoResponseDTO(Integer id, String status, LocalDateTime criadoEm,
                                 UsuarioResponseDTO usuario, EventoResponseDTO evento,
                                 String nomeParticipante, String cpfParticipante, String telefoneParticipante) {
        this.id = id;
        this.status = status;
        this.criadoEm = criadoEm;
        this.usuario = usuario;
        this.evento = evento;
        this.nomeParticipante = nomeParticipante;
        this.cpfParticipante = cpfParticipante;
        this.telefoneParticipante = telefoneParticipante;
    }

    public static InscricaoResponseDTO fromEntity(Inscricao inscricao) {
        return new InscricaoResponseDTO(
            inscricao.getId(),
            inscricao.getStatus(),
            inscricao.getCriadoEm(),
            UsuarioResponseDTO.fromEntity(inscricao.getResponsavel()),
            EventoResponseDTO.fromEntity(inscricao.getEvento()),
            inscricao.getNomeParticipante(),
            inscricao.getCpfParticipante(),
            inscricao.getTelefoneParticipante()
        );
    }

    public Integer getId() { return id; }
    public String getStatus() { return status; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public UsuarioResponseDTO getUsuario() { return usuario; }
    public EventoResponseDTO getEvento() { return evento; }
    public String getNomeParticipante() { return nomeParticipante; }
    public String getCpfParticipante() { return cpfParticipante; }
    public String getTelefoneParticipante() { return telefoneParticipante; }
}