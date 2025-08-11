package com.matheusbiesek.todolist.spring_todo.service;

import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(UUID id) {
        try {
            return usuarioRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByNomeUsuario(String nomeUsuario) {
        try {
            return usuarioRepository.findByNomeUsuario(nomeUsuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário por nome: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        try {
            return usuarioRepository.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário por email: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Usuario save(Usuario usuario) {
        try {
            validateUniqueFields(usuario);
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar usuário: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Usuario update(Usuario usuario) {
        try {
            if (!usuarioRepository.existsById(usuario.getUsuarioId())) {
                throw new RuntimeException("Usuário não encontrado para atualização");
            }
            return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar usuário: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteById(UUID id) {
        try {
            if (!usuarioRepository.existsById(id)) {
                throw new RuntimeException("Usuário não encontrado para exclusão");
            }
            usuarioRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar usuário: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public boolean existsByNomeUsuario(String nomeUsuario) {
        return usuarioRepository.existsByNomeUsuario(nomeUsuario);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    private void validateUniqueFields(Usuario usuario) {
        if (usuarioRepository.existsByNomeUsuario(usuario.getNomeUsuario())) {
            throw new RuntimeException("Nome de usuário já existe");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Email já existe");
        }
    }
}