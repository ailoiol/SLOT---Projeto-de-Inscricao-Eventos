package com.yasmin.inscricao_eventos.controller;

import com.yasmin.inscricao_eventos.dto.LoginRequest;
import com.yasmin.inscricao_eventos.dto.UsuarioResponseDTO;
import com.yasmin.inscricao_eventos.exception.AutenticacaoException;
import com.yasmin.inscricao_eventos.model.Usuario;
import com.yasmin.inscricao_eventos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public UsuarioResponseDTO login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new AutenticacaoException("Email ou senha inválidos"));

        if (!usuario.getSenhaHash().equals(request.getSenha())) {
            throw new AutenticacaoException("Email ou senha inválidos");
        }

        return UsuarioResponseDTO.fromEntity(usuario);
    }
}