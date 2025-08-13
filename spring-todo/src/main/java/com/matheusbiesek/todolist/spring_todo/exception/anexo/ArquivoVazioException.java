package com.matheusbiesek.todolist.spring_todo.exception.anexo;

public class ArquivoVazioException extends RuntimeException {
    
    public ArquivoVazioException(String message) {
        super(message);
    }
    
    public ArquivoVazioException(String message, Throwable cause) {
        super(message, cause);
    }
}