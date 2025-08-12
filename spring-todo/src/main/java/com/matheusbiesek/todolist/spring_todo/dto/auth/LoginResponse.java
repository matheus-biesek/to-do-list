package com.matheusbiesek.todolist.spring_todo.dto.auth;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private UUID usuarioId;
    private String nomeUsuario;
}