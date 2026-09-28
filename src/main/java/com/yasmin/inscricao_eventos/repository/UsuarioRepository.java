package com.yasmin.inscricao_eventos.repository;

import com.yasmin.inscricao_eventos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}