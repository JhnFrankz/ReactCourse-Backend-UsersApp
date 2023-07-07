package com.jhnfrankz.backend.usersapp.backendusersapp.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.*;

import static com.jhnfrankz.backend.usersapp.backendusersapp.auth.TokenJwtConfig.*;

// El BasicAuthenticationFilter se ejecuta despues de que el usuario se autentica
// se ejecuta siempre en todos los request(POST, GET, PUT, DELETE, etc)
public class JwtValidationFilter extends BasicAuthenticationFilter {
    public JwtValidationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    // este metodo se ejecuta siempre en todos los request(POST, GET, PUT, DELETE, etc)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String header = request.getHeader((HEADER_AUTHORIZATION));

        if (header == null || !header.startsWith(PREFIX_TOKEN)) {
            chain.doFilter(request, response); // si no hay token, continua con la cadena de filtros
            return;
        }

        // obtiene el token que está codificado en base64, tendremos que decodificarlo
        String token = header.replace(PREFIX_TOKEN, "");
        byte[] tokenDecodeBytes = Base64.getDecoder().decode(token);
        String tokenDecode = new String(tokenDecodeBytes);

        String[] tokenArr = tokenDecode.split("\\.");
        System.out.println("tokenArr: " + Arrays.toString(tokenArr));
        String secret = tokenArr[0];
        String username = tokenArr[1];

        // si el token es valido, se autentica al usuario
        if (SECRET_KEY.equals(secret)) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            // agregamos el rol del usuario que se autentico en el token
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // no se usa el password ya que solo se usa para autenticar al usuario y no para autorizarlo
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // se autentica al usuario, dejamos pasar al usuario a este recurso protegido
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            Map<String, String> body = new HashMap<>();
            body.put("message", "El token JWT no es valido!");
            //
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(403);
            response.setContentType("application/json");
        }

    }
}
