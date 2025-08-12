package com.matheusbiesek.todolist.spring_todo.dto.auth;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private UUID usuarioId;
    private String nomeUsuario;
    private String email;
}