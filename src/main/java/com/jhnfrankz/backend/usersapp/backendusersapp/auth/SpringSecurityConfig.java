package com.jhnfrankz.backend.usersapp.backendusersapp.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    /*
     * Cuando anotamos un metodo con @Bean y está dentro de una clase anotada
     * con @Configuration, lo que devuelve ese metodo se guarda en el contexto de
     * Spring como un bean. parecido a @Component
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authRules -> authRules
                .requestMatchers(HttpMethod.GET, "/users").permitAll() // la ruta /users es publica
                .anyRequest().authenticated()) // el resto de rutas deben estar autenticadas
                .csrf(config -> config.disable()) // en api rest no se usa csrf
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // en api rest no se usa, lo ponemos en sin estado
                // el login lo manejamos con jwt
                .build();
    }
}
