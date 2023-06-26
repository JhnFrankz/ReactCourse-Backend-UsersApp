package com.jhnfrankz.backend.usersapp.backendusersapp.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;
import com.jhnfrankz.backend.usersapp.backendusersapp.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    // cuando se hace una peticion get a la ruta /users, se ejecuta este metodo
    @GetMapping
    public List<User> list() {
        return service.findAll();
    }

    // ya que el id puede cambiar, se debe usar el path variable
    // el path variable se usa para que el id que se recibe en la ruta, se guarde en
    // la variable id del metodo, tanto el nombre de la ruta como el nombre de la
    // variable deben ser iguales
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<User> userOptional = service.findById(id);

        if (userOptional.isPresent()) {
            // si el usuario existe, se retorna un response entity con el usuario y el
            // codigo 200
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        // si no existe, se retorna un response entity con el codigo 404
        return ResponseEntity.notFound().build();
    }

    // cuando se haga post a /users, se ejecuta este metodo
    // Indicamos que el user se recibe en el body de la peticion
    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        // retornamos el usuario en el body y el codigo 201
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    // el user viene en el body de la peticion
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody User user, @PathVariable Long id) {
        Optional<User> o = service.findById(id);
        if (o.isPresent()) {
            User userDb = o.orElseThrow();
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(service.save(userDb));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        Optional<User> o = service.findById(id);

        if (o.isPresent()) {
            service.remove(id);
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.notFound().build(); // 404
    }
}
