package com.matheusbiesek.todolist.spring_todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private UUID usuarioId;
    private String nomeUsuario;
}