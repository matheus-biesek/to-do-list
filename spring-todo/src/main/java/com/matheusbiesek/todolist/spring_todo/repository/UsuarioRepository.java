package com.matheusbiesek.todolist.spring_todo.repository;

import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByNomeUsuario(String nomeUsuario);

    Optional<Usuario> findByEmail(String email);

    boolean existsByNomeUsuario(String nomeUsuario);

    boolean existsByEmail(String email);
}