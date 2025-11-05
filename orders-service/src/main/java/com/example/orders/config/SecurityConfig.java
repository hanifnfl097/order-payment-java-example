package com.example.orders.config;

import com.example.orders.security.JwtAuthenticationFilter;
import com.example.orders.security.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JpaUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          JpaUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API → CSRF dimatikan
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless (tidak pakai HTTP Session)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // pakai UserDetailsService kita (opsional tapi oke)
                .userDetailsService(userDetailsService)

                .authorizeHttpRequests(auth -> auth
                        // ==== PUBLIC ENDPOINTS ====
                        // register & login
                        .requestMatchers("/api/auth/**").permitAll()
                        // katalog produk GET tetap public
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // ==== ADMIN ONLY (authority = 'ADMIN') ====
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAuthority("ADMIN")

                        // ==== ENDPOINT LAIN HARUS AUTHENTICATED ====
                        .anyRequest().authenticated()
                )

                // Tambahkan JWT filter sebelum UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
