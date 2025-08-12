package com.matheusbiesek.todolist.spring_todo.exception.tarefa;

public class TarefaComSubtarefasPendentesException extends RuntimeException {

    public TarefaComSubtarefasPendentesException(String message) {
        super(message);
    }

    public TarefaComSubtarefasPendentesException(Long tarefaId) {
        super("Não é possível concluir a tarefa " + tarefaId + " pois ainda há subtarefas pendentes");
    }
}