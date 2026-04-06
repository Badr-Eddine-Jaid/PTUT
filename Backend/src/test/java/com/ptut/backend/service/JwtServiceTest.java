package com.ptut.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", "ptut_local_dev_secret_change_me_please_1234567890");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_extractUsername_OK() {
        String token = jwtService.generateToken("jean@test.com", "AMBASSADEUR");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("jean@test.com");
    }

    @Test
    void generateToken_contientRole() {
        String token = jwtService.generateToken("jean@test.com", "ADMIN");

        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertThat(role).isEqualTo("ADMIN");
    }

    @Test
    void isTokenValid_tokenValide_retourneTrue() {
        String token = jwtService.generateToken("jean@test.com", "AMBASSADEUR");

        UserDetails userDetails = User.withUsername("jean@test.com")
                .password("irrelevant")
                .authorities(Collections.emptyList())
                .build();

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_mauvaisUtilisateur_retourneFalse() {
        String token = jwtService.generateToken("jean@test.com", "AMBASSADEUR");

        UserDetails autreUser = User.withUsername("autre@test.com")
                .password("irrelevant")
                .authorities(Collections.emptyList())
                .build();

        assertThat(jwtService.isTokenValid(token, autreUser)).isFalse();
    }

    @Test
    void tokenExpire_throwsException() {
        JwtService expiredService = new JwtService();
        ReflectionTestUtils.setField(expiredService, "jwtSecret", "ptut_local_dev_secret_change_me_please_1234567890");
        ReflectionTestUtils.setField(expiredService, "jwtExpiration", -1000L);

        String token = expiredService.generateToken("jean@test.com", "AMBASSADEUR");

        assertThatThrownBy(() -> jwtService.extractUsername(token))
                .isInstanceOf(Exception.class);
    }
}
