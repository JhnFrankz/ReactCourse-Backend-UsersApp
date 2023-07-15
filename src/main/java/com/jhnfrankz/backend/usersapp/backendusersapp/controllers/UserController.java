package com.jhnfrankz.backend.usersapp.backendusersapp.controllers;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.request.UserRequest;
import com.jhnfrankz.backend.usersapp.backendusersapp.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// las rutas públicas son las que no necesitan autenticación
// las rutas privadas son las que necesitan autenticación

@RestController
@RequestMapping("/users")
@CrossOrigin(originPatterns = "*") // acepta peticiones de cualquier origen, en este caso amazon
public class UserController {

    @Autowired
    private UserService service;

    // cuando se hace una peticion get a la ruta /users, se ejecuta este metodo
    @GetMapping
    public List<UserDto> list() {
        return service.findAll();
    }

    // el page parte en 0
    @GetMapping("/page/{page}")
    public Page<UserDto> list(@PathVariable Integer page) {
        // PageRequest.of recibe el numero de pagina y el numero de elementos por pagina
        Pageable pageable = PageRequest.of(page, 8);
        return service.findAll(pageable);
    }

    // ya que el id puede cambiar, se debe usar el path variable
    // el path variable se usa para que el id que se recibe en la ruta, se guarde en
    // la variable id del metodo, tanto el nombre de la ruta como el nombre de la
    // variable deben ser iguales
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<UserDto> userOptional = service.findById(id);

        if (userOptional.isPresent()) {
            // si el usuario existe, se retorna un response entity con el usuario y el
            // codigo 200
            return ResponseEntity.ok(userOptional.orElseThrow());
        }

        // si no existe, se retorna un response entity con el codigo 404
        return ResponseEntity.notFound().build();
    }

    /*
     * Con @Valid indicamos que se debe validar el objeto user de acuerdo a las
     * anotaciones que tiene en su clase
     * El BindingResult es para obtener los errores de validacion, siempre debe ir
     * despues del objeto a validar
     */

    // cuando se haga post a /users, se ejecuta este metodo
    // Indicamos que el user se recibe en el body de la peticion
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        // retornamos el usuario en el body y el codigo 201
        // el save puede dar error en caso de que ya exista un usuario
        // y lanzaria un error 500, que se controla con el try catch
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }

    // el user viene en el body de la peticion
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody UserRequest user, BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            return validation(result);
        }

        Optional<UserDto> o = service.update(user, id);
        if (o.isPresent()) {
            // retornamos el usuario actualizado y el codigo 201
            return ResponseEntity.status(HttpStatus.CREATED).body(o.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        Optional<UserDto> o = service.findById(id);

        if (o.isPresent()) {
            service.remove(id);
            return ResponseEntity.noContent().build(); // 204
        }

        return ResponseEntity.notFound().build(); // 404
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();

        result.getFieldErrors().forEach(err -> {
            errors.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }
}
