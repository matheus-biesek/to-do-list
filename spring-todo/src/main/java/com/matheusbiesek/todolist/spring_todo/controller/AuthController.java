package com.matheusbiesek.todolist.spring_todo.controller;

import com.matheusbiesek.todolist.spring_todo.dto.auth.LoginRequest;
import com.matheusbiesek.todolist.spring_todo.dto.auth.LoginResponse;
import com.matheusbiesek.todolist.spring_todo.dto.auth.RegisterRequest;
import com.matheusbiesek.todolist.spring_todo.dto.auth.RegisterResponse;
import com.matheusbiesek.todolist.spring_todo.entity.Usuario;
import com.matheusbiesek.todolist.spring_todo.repository.UsuarioRepository;
import com.matheusbiesek.todolist.spring_todo.security.CustomUserDetails;
import com.matheusbiesek.todolist.spring_todo.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Fazer login", description = "Autentica um usuário e retorna um token JWT via cookie")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    value = "{\"message\":\"Login realizado com sucesso\",\"usuarioId\":\"123e4567-e89b-12d3-a456-426614174000\",\"nomeUsuario\":\"usuario123\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Credenciais inválidas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    value = "{\"message\":\"Credenciais inválidas\",\"usuarioId\":null,\"nomeUsuario\":null}"
                )
            )
        )
    })
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

    @Operation(summary = "Fazer logout", description = "Remove o token JWT do cookie HttpOnly")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Logout realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "string"),
                examples = @ExampleObject(
                    value = "\"Logout realizado com sucesso\""
                )
            )
        )
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("access-token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    @Operation(summary = "Registrar usuário", description = "Cria uma nova conta de usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Usuário registrado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterResponse.class),
                examples = @ExampleObject(
                    value = "{\"message\":\"Usuário registrado com sucesso\",\"usuarioId\":\"123e4567-e89b-12d3-a456-426614174000\",\"nomeUsuario\":\"usuario123\",\"email\":\"usuario@email.com\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Nome de usuário já existe",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterResponse.class),
                examples = @ExampleObject(
                    value = "{\"message\":\"Nome de usuário já existe\",\"usuarioId\":null,\"nomeUsuario\":null,\"email\":null}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Email já está em uso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterResponse.class),
                examples = @ExampleObject(
                    value = "{\"message\":\"Email já está em uso\",\"usuarioId\":null,\"nomeUsuario\":null,\"email\":null}"
                )
            )
        )
    })
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