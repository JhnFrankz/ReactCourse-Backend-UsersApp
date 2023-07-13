package com.jhnfrankz.backend.usersapp.backendusersapp.services;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.IUser;
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
        // Convertimos el Optional<User> a Optional<UserDto>, sino existe el usuario, devolvemos un Optional vacío
        return repository.findById(id)
                .map(u -> DtoMapperUser.builder().setUser(u).build());
    }

    @Override
    @Transactional
    public UserDto save(User user) {
        // encriptamos la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(getRoles(user));

        return DtoMapperUser.builder().setUser(repository.save(user)).build();
    } // cada vez que se guarda un usuario, se encripta la contraseña

    @Override
    @Transactional
    public Optional<UserDto> update(UserRequest user, Long id) {

        Optional<User> o = repository.findById(id);
        User userOptional = null;

        if (o.isPresent()) {

            User userDb = o.orElseThrow();
            userDb.setRoles(getRoles(user));
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

    private List<Role> getRoles(IUser user) {

        Optional<Role> ou = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        // si existe el rol, lo agregamos a la lista de roles
        if (ou.isPresent()) {
            roles.add(ou.orElseThrow());
        }

        // si está marcado el checkbox de administrador, se le asigna el rol de administrador
        if (user.isAdmin()) {
            Optional<Role> oa = roleRepository.findByName("ROLE_ADMIN");
            if (oa.isPresent()) {
                roles.add(oa.orElseThrow());
            }
        }

        return roles;
    }
}
