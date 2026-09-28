package com.yasmin.inscricao_eventos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class TratadorDeExcecoes {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException excecao) {
        Map<String, String> corpo = Map.of("erro", excecao.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, String>> tratarRegraDeNegocio(RegraDeNegocioException excecao) {
        Map<String, String> corpo = Map.of("erro", excecao.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo);
    }
}