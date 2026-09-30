package com.yasmin.inscricao_eventos.controller;


import com.yasmin.inscricao_eventos.exception.RecursoNaoEncontradoException;
import com.yasmin.inscricao_eventos.dto.UsuarioResponseDTO;
import com.yasmin.inscricao_eventos.model.Usuario;
import com.yasmin.inscricao_eventos.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
            .stream()
            .map(UsuarioResponseDTO::fromEntity)
            .toList();
}

    @PostMapping
    public UsuarioResponseDTO criar(@Valid @RequestBody Usuario usuario) {
        usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        Usuario salvo = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(salvo);
}
       
    @PutMapping("/{id}")
    public UsuarioResponseDTO atualizar(@PathVariable Integer id, @Valid @RequestBody Usuario dadosAtualizados) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com id " + id + " não encontrado"));

        usuario.setNome(dadosAtualizados.getNome());
        usuario.setCpf(dadosAtualizados.getCpf());
        usuario.setTelefone(dadosAtualizados.getTelefone());
        usuario.setEmail(dadosAtualizados.getEmail());
        usuario.setTipo(dadosAtualizados.getTipo());
        usuario.setPerfilCompleto(dadosAtualizados.isPerfilCompleto());

        if (dadosAtualizados.getSenhaHash() != null && !dadosAtualizados.getSenhaHash().isBlank()) {
            usuario.setSenhaHash(passwordEncoder.encode(dadosAtualizados.getSenhaHash()));
    }

    Usuario atualizado = usuarioRepository.save(usuario);
    return UsuarioResponseDTO.fromEntity(atualizado);
}

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Integer id) {
        usuarioRepository.deleteById(id);
    }

}