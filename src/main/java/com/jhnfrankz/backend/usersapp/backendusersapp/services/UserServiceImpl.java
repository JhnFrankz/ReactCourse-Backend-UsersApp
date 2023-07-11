package com.jhnfrankz.backend.usersapp.backendusersapp.services;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.dto.mapper.DtoMapperUser;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.Role;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.request.UserRequest;
import com.jhnfrankz.backend.usersapp.backendusersapp.repositories.RoleRepository;
import com.jhnfrankz.backend.usersapp.backendusersapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    // Imyeplementamos la interfaz PasswordEncoder para encriptar la contraseña
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {

        List<User> users = (List<User>) repository.findAll();

        return users
                .stream()
                .map(u -> DtoMapperUser.builder().setUser(u).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {

        Optional<User> o = repository.findById(id);
        if (o.isPresent()) {
            return Optional.of(
                    DtoMapperUser.builder().setUser(o.orElseThrow()).build()
            );
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public UserDto save(User user) {
        // encriptamos la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // buscamos el rol por nombre
        Optional<Role> o = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        // si existe el rol, lo agregamos a la lista de roles
        if (o.isPresent()) {
            roles.add(o.orElseThrow());
        }
        // cada vez que se guarda un usuario, se le asigna el rol de usuario
        user.setRoles(roles);

        return DtoMapperUser.builder().setUser(repository.save(user)).build();
    } // cada vez que se guarda un usuario, se encripta la contraseña

    @Override
    @Transactional
    public Optional<UserDto> update(UserRequest user, Long id) {

        Optional<User> o = repository.findById(id);
        User userOptional = null;

        if (o.isPresent()) {
            User userDb = o.orElseThrow();
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            // guardamos en la bd el usuario actualizado y en userOptional
            userOptional = repository.save(userDb);
        }

        return Optional.ofNullable(DtoMapperUser.builder().setUser(userOptional).build());
    }

    @Override
    @Transactional
    public void remove(Long id) {

        repository.deleteById(id);
    }

}
