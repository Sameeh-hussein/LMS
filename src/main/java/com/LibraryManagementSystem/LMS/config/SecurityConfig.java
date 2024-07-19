package com.LibraryManagementSystem.LMS.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/auth/**",
                                "/api/roles/**",
                                "/api/users/**",
                                "/api/borrows/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/Books",
                                "/api/authors",
                                "/api/categories").hasRole("LIBRARIAN")

                        .requestMatchers(HttpMethod.PUT, "/api/Books/",
                                "/api/authors/").hasRole("LIBRARIAN")

                        .requestMatchers(HttpMethod.DELETE, "/api/Books/",
                                "/api/categories/",
                                "/api/authors/").hasRole("LIBRARIAN")

                        .requestMatchers(HttpMethod.GET, "/api/Books/**",
                                "/api/categories/**",
                                "/api/authors/**").permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}