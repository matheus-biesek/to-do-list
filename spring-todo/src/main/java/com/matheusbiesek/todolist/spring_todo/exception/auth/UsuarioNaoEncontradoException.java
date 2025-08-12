package com.matheusbiesek.todolist.spring_todo.exception.auth;

import java.util.UUID;

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(String message) {
        super(message);
    }

    public UsuarioNaoEncontradoException(UUID id) {
        super("Usuário não encontrado com ID: " + id);
    }
}