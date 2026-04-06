package com.ptut.backend.service;

import com.ptut.backend.dto.AuthResponse;
import com.ptut.backend.dto.LoginRequest;
import com.ptut.backend.dto.RegisterRequest;
import com.ptut.backend.entity.Role;
import com.ptut.backend.entity.Utilisateur;
import com.ptut.backend.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // ─── register ────────────────────────────────────────────────────────────

    @Test
    void register_OK() {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Dupont");
        request.setPrenom("Jean");
        request.setEmail("jean@test.com");
        request.setPassword("motdepasse");

        when(utilisateurRepository.findByEmail("jean@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("motdepasse")).thenReturn("hashed");

        Utilisateur saved = new Utilisateur();
        saved.setEmail("jean@test.com");
        saved.setRole(Role.AMBASSADEUR);
        when(utilisateurRepository.save(any())).thenReturn(saved);
        when(jwtService.generateToken("jean@test.com", "AMBASSADEUR")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("jean@test.com");
        verify(passwordEncoder).encode("motdepasse");
        verify(utilisateurRepository).save(any(Utilisateur.class));
    }

    @Test
    void register_emailDejaUtilise_throwsBadRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("jean@test.com");
        request.setPassword("motdepasse");

        when(utilisateurRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(new Utilisateur()));

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email déjà utilisé");

        verify(utilisateurRepository, never()).save(any());
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    void login_OK() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jean@test.com");
        request.setPassword("motdepasse");

        Utilisateur user = new Utilisateur();
        user.setEmail("jean@test.com");
        user.setPassword("hashed");
        user.setRole(Role.AMBASSADEUR);

        when(utilisateurRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("motdepasse", "hashed")).thenReturn(true);
        when(jwtService.generateToken("jean@test.com", "AMBASSADEUR")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("jean@test.com");
    }

    @Test
    void login_mauvaisMotDePasse_throwsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jean@test.com");
        request.setPassword("mauvais");

        Utilisateur user = new Utilisateur();
        user.setEmail("jean@test.com");
        user.setPassword("hashed");
        user.setRole(Role.AMBASSADEUR);

        when(utilisateurRepository.findByEmail("jean@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("mauvais", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("mot de passe incorrect");
    }

    @Test
    void login_emailInconnu_throwsUnauthorized() {
        LoginRequest request = new LoginRequest();
        request.setEmail("inconnu@test.com");
        request.setPassword("motdepasse");

        when(utilisateurRepository.findByEmail("inconnu@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("mot de passe incorrect");

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
}
