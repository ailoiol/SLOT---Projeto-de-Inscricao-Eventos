package com.yasmin.inscricao_eventos.dto;

import com.yasmin.inscricao_eventos.model.Evento;
import java.time.LocalDateTime;

public class EventoResponseDTO {

    private Integer id;
    private String titulo;
    private String descricao;
    private LocalDateTime dataEvento;
    private Integer vagasTotais;
    private Integer vagasOcupadas;
    private String status;
    private UsuarioResponseDTO organizador;

    public EventoResponseDTO(Integer id, String titulo, String descricao, LocalDateTime dataEvento,
                              Integer vagasTotais, Integer vagasOcupadas, String status,
                              UsuarioResponseDTO organizador) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataEvento = dataEvento;
        this.vagasTotais = vagasTotais;
        this.vagasOcupadas = vagasOcupadas;
        this.status = status;
        this.organizador = organizador;
    }

    public static EventoResponseDTO fromEntity(Evento evento) {
    return new EventoResponseDTO(
        evento.getId(),
        evento.getTitulo(),
        evento.getDescricao(),
        evento.getDataEvento(),
        evento.getVagasTotais(),
        evento.getVagasOcupadas(),
        evento.getStatus(),
        UsuarioResponseDTO.fromEntity(evento.getOrganizador())
    );
}

    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataEvento() { return dataEvento; }
    public Integer getVagasTotais() { return vagasTotais; }
    public Integer getVagasOcupadas() { return vagasOcupadas; }
    public String getStatus() { return status; }
    public UsuarioResponseDTO getOrganizador() { return organizador; }
}