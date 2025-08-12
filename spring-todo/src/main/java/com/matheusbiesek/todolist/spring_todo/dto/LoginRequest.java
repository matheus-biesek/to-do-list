package com.matheusbiesek.todolist.spring_todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank
    private String nomeUsuario;
    
    @NotBlank
    private String senha;
}