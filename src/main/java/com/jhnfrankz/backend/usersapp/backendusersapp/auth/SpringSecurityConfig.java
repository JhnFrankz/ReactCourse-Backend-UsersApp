package com.jhnfrankz.backend.usersapp.backendusersapp.auth;

import com.jhnfrankz.backend.usersapp.backendusersapp.auth.filters.JwtAuthenticationFilter;
import com.jhnfrankz.backend.usersapp.backendusersapp.auth.filters.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// esta clase tendrá metodos que crearan instancias de objetos y los guardaran en el contexto
@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    PasswordEncoder passwordEncoder() {
        // retornamos el ByCryptPasswordEncoder para que se encripten las contraseñas
        return new BCryptPasswordEncoder();
    }

    // con Bean indicamos que este método devuelve una instancia que queremos que se guarde en el contexto de Spring
    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        // a partir de la configuracion de autenticacionConfiguration, devolvemos el AuthenticationManager
        // y lo guardamos como un bean en el contexto de Spring
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authRules -> authRules
                        .requestMatchers(HttpMethod.GET, "/users").permitAll() // la ruta /users es publica
                        .anyRequest().authenticated()) // el resto de rutas deben estar autenticadas
                // agregamos el filtro que se ejecuta cuando se hace un POST a /login
                // y que se encarga de autenticar al usuario
                .addFilter(new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
                .csrf(config -> config.disable()) // en api rest no se usa csrf
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // en api rest no se usa, lo ponemos en sin estado
                // el login lo manejamos con jwt
                .build();
    }
}