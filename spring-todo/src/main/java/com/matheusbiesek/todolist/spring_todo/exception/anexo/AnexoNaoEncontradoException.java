package com.matheusbiesek.todolist.spring_todo.exception.anexo;

public class AnexoNaoEncontradoException extends RuntimeException {
    
    public AnexoNaoEncontradoException(String message) {
        super(message);
    }
    
    public AnexoNaoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}