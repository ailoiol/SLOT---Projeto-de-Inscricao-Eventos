package com.yasmin.inscricao_eventos.dto;

import com.yasmin.inscricao_eventos.model.Usuario;
import java.time.LocalDateTime;

public class UsuarioResponseDTO {

    private Integer id;
    private String nome;
    private String email;
    private String tipo;
    private boolean perfilCompleto;
    private LocalDateTime criadoEm;

    public UsuarioResponseDTO(Integer id, String nome, String email, String tipo, boolean perfilCompleto, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipo = tipo;
        this.perfilCompleto = perfilCompleto;
        this.criadoEm = criadoEm;
    }

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTipo(),
            usuario.isPerfilCompleto(),
            usuario.getCriadoEm()
        );
    }

    public Integer getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getTipo() { return tipo; }
    public boolean isPerfilCompleto() { return perfilCompleto; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}