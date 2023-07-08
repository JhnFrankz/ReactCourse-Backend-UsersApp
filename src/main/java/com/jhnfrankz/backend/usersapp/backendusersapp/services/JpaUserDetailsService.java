package com.jhnfrankz.backend.usersapp.backendusersapp.services;

import com.jhnfrankz.backend.usersapp.backendusersapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Los @Service son componentes de Spring que se encargan de la logica de negocio
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    // el UserDetailsService se encarga de buscar al usuario en la base de datos por detrás
    // y luego se usa el AuthenticationManager para autenticar al usuario con el UserDetailsService
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Porqué se usa el User de Spring Security y no el User de la entidad User?
        // porque el User de Spring Security implementa UserDetails que es una interfaz
        // que se encarga de manejar la seguridad de los usuarios
        Optional<com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User> o = repository.findByUsername(username);
        //simulamos que buscamos el usuario en la base de datos
        if (!o.isPresent()) {
            throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema", username));
        }

        com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User user = o.orElseThrow();

        List<GrantedAuthority> authorities = new ArrayList<>();
        // siempre los roles deben empezar con ROLE_
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // retornamos un UserDetails que es una clase de Spring Security
        //luego por detras esto pasa al AuthenticationManager y este se encarga de autenticar al usuario
        // la clave es 12345 encriptada con bcrypt
        return new User(user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }
}
