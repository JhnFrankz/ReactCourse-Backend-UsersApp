package com.jhnfrankz.backend.usersapp.backendusersapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.request.UserRequest;
import com.jhnfrankz.backend.usersapp.backendusersapp.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    // Imyeplementamos la interfaz PasswordEncoder para encriptar la contraseña
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return (List<User>) repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {
        // encriptamos la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    } // cada vez que se guarda un usuario, se encripta la contraseña

    @Override
    @Transactional
    public Optional<User> update(UserRequest user, Long id) {
        Optional<User> o = this.findById(id);
        User userOptional = null;

        if (o.isPresent()) {
            User userDb = o.orElseThrow();
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            // guardamos en la bd el usuario actualizado y en userOptional
            userOptional = this.save(userDb);
        }

        return Optional.ofNullable(userOptional);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        repository.deleteById(id);
    }

}
