package com.jhnfrankz.backend.usersapp.backendusersapp.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhnfrankz.backend.usersapp.backendusersapp.auth.SimpleGrantedAuthorityJsonCreator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

        // si el token es valido, se autentica al usuario
        try {
            // claims es la informacion que se envio en el token luego del filtro de autenticacion
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object authoritiesClaims = claims.get("authorities");

            // el getSubject() es el username del usuario que se autentico en el token
            String username = claims.getSubject();
            Object username2 = claims.get("username");
            System.out.println("username: " + username);
            System.out.println("username2: " + username2);

            // convertimos nuestro objeto authoritiesClaims de estructura json a una lista de GrantedAuthority
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.asList(new ObjectMapper()
                            // con el addMixIn mezclamos la clase SimpleGrantedAuthority con la clase
                            // SimpleGrantedAuthorityJsonCreator, le modificamos el constructor para que pase el
                            // nombre del rol de la propiedad "authority" del json
                            .addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
                            .readValue(authoritiesClaims.toString().getBytes(),
                                    SimpleGrantedAuthority[].class));

            // no se usa el password ya que solo se usa para autenticar al usuario y no para autorizarlo
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);

            // se autentica al usuario, dejamos pasar al usuario a este recurso protegido
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (JwtException e) {
            Map<String, String> body = new HashMap<>();
            body.put("error", e.getMessage());
            body.put("message", "El token JWT no es valido!");

            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.setStatus(401); // 401 Unauthorized
            response.setContentType("application/json");
        }

    }
}
