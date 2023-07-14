package com.jhnfrankz.backend.usersapp.backendusersapp.auth;

import com.jhnfrankz.backend.usersapp.backendusersapp.auth.filters.JwtAuthenticationFilter;
import com.jhnfrankz.backend.usersapp.backendusersapp.auth.filters.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

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
                        .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                        // las rutas /users/** solo pueden ser accedidas por usuarios con rol ADMIN, exceptuando
                        // las rutas definidas anteriormente
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        /*.requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/{id}").hasRole("ADMIN")*/
                        .anyRequest().authenticated()) // el resto de rutas deben estar autenticadas
                // agregamos el filtro que se ejecuta cuando se hace un POST a /login
                // y que se encarga de autenticar al usuario
                .addFilter(new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))
                // agregamos el filtro que se ejecuta en todas las peticiones y que se encarga de validar el token
                .addFilter(new JwtValidationFilter(authenticationConfiguration.getAuthenticationManager()))
                .csrf(config -> config.disable()) // en api rest no se usa csrf
                .sessionManagement(management ->
                        management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // en api rest no se usa, lo ponemos en sin estado
                // el login lo manejamos con jwt
                .build();
    }

    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter() {

        FilterRegistrationBean<CorsFilter> bean =
                new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource()));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedOriginPatterns(Arrays.asList("*")); // para cualquier origen
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}