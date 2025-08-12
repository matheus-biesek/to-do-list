package com.matheusbiesek.todolist.spring_todo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private UUID usuarioId;
    private String nomeUsuario;
    private String email;
}