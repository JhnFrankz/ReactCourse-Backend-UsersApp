package com.jhnfrankz.backend.usersapp.backendusersapp.auth.filters;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jhnfrankz.backend.usersapp.backendusersapp.auth.TokenJwtConfig.*;

// por debajo se maneja una ruta url /login
// Se ejecuta este filtro cuando se hace un POST a /login
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    // el AuthenticationManager se encarga de autenticar al usuario
    // no lo creamos nosotros, lo crea Spring
    // por debajo se usa el UserDetailsService para buscar al usuario en la base de datos
    private AuthenticationManager authenticationManager;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    // se ejecuta cuando se hace un POST a /login
    // lo que hace es leer el body de la peticion y convertirlo a un objeto User
    // luego crea un UsernamePasswordAuthenticationToken con el username y password
    // del objeto User y lo pasa al AuthenticationManager para que lo autentique
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        User user = null;
        String username = null;
        String password = null;

        try {
            // leemos el body de la peticion y lo convertimos a un objeto User
            // el ObjectMapper nos permite convertir un json a un objeto
            // el readValue recibe un InputStream y una clase a la que se convertira el json
            user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            username = user.getUsername();
            password = user.getPassword();

            /*logger.info("Username desde request InputSream (raw): " + username);
            logger.info("Password desde request InputSream (raw): " + password);*/
        } catch (StreamReadException e) {
            e.printStackTrace();
        } catch (DatabindException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // creamos un UsernamePasswordAuthenticationToken con el username y password
        // para que lo autentique el AuthenticationManager
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // pasamos el authToken al AuthenticationManager para que lo autentique
        return authenticationManager.authenticate(authToken);
        // se usa por debajo el UserDetailsService para buscar al usuario en la base de datos
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        // Obtenemos el username del usuario autenticado
        String username = ((org.springframework.security.core.userdetails.User) authResult.getPrincipal())
                .getUsername();
        String token = Jwts.builder()
                .setSubject(username)
                .signWith(SECRET_KEY)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000L)) // 1 hora
                .compact();

        // agregamos el token al header de la respuesta
        response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + token);

        // creamos un objeto para convertirlo a json
        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("message", String.format("Hola %s, has iniciado sesion con exito", username));
        body.put("username", username);

        // convertimos el body a json y lo escribimos en el response
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Error en la autenticacion username o password incorrecto");
        body.put("error", failed.getMessage());

        // convertimos el body a json y lo escribimos en el response
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        // enviamos el status 401 (Unauthorized)
        response.setStatus(401);
        response.setContentType("application/json");
    }
}