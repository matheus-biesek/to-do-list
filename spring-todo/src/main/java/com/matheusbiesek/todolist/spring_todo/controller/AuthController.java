package com.matheusbiesek.todolist.spring_todo.controller;

import com.matheusbiesek.todolist.spring_todo.dto.LoginRequest;
import com.matheusbiesek.todolist.spring_todo.dto.LoginResponse;
import com.matheusbiesek.todolist.spring_todo.dto.RegisterRequest;
import com.matheusbiesek.todolist.spring_todo.dto.RegisterResponse;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.repository.UsuarioRepository;
import com.matheusbiesek.todolist.spring_todo.security.CustomUserDetails;
import com.matheusbiesek.todolist.spring_todo.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, 
                                               HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getNomeUsuario(), 
                    loginRequest.getSenha()
                )
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(userDetails.getUserId());

            Cookie cookie = new Cookie("access-token", accessToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(15 * 60); // 15 minutes
            response.addCookie(cookie);

            return ResponseEntity.ok(new LoginResponse(
                "Login realizado com sucesso",
                userDetails.getUserId(),
                userDetails.getUsername()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new LoginResponse("Credenciais inválidas", null, null)
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("access-token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (usuarioRepository.existsByNomeUsuario(registerRequest.getNomeUsuario())) {
                return ResponseEntity.badRequest().body(
                    new RegisterResponse("Nome de usuário já existe", null, null, null)
                );
            }

            if (usuarioRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest().body(
                    new RegisterResponse("Email já está em uso", null, null, null)
                );
            }

            Usuario usuario = new Usuario();
            usuario.setNomeUsuario(registerRequest.getNomeUsuario());
            usuario.setEmail(registerRequest.getEmail());
            usuario.setSenhaHash(passwordEncoder.encode(registerRequest.getSenha()));

            Usuario usuarioSalvo = usuarioRepository.save(usuario);

            return ResponseEntity.ok(new RegisterResponse(
                "Usuário registrado com sucesso",
                usuarioSalvo.getUsuarioId(),
                usuarioSalvo.getNomeUsuario(),
                usuarioSalvo.getEmail()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new RegisterResponse("Erro ao registrar usuário: " + e.getMessage(), null, null, null)
            );
        }
    }
}