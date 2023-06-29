package com.jhnfrankz.backend.usersapp.backendusersapp.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class JpaUserDetailsService implements UserDetailsService {

    // el UserDetailsService se encarga de buscar al usuario en la base de datos por detrás
    // y luego se usa el AuthenticationManager para autenticar al usuario con el UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //simulamos que buscamos el usuario en la base de datos
        if (!username.equals("admin")) {
            throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema", username));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        // siempre los roles deben empezar con ROLE_
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // retornamos un UserDetails que es una clase de Spring Security
        //luego por detras esto pasa al AuthenticationManager y este se encarga de autenticar al usuario
        return new User(username,
                "12345",
                true,
                true,
                true,
                true,
                authorities);
    }
}
