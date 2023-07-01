package com.jhnfrankz.backend.usersapp.backendusersapp.auth.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

// El BasicAuthenticationFilter se ejecuta despues de que el usuario se autentica
// se ejecuta siempre en todos los request(POST, GET, PUT, DELETE, etc)
public class JwtValidationFilter extends BasicAuthenticationFilter {
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader(("Authorization"));

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response); // si no hay token, continua con la cadena de filtros
            return;
        }

        // obtiene el token que está codificado en base64, tendremos que decodificarlo
        String token = header.replace("Bearer ", "");
    }
}
