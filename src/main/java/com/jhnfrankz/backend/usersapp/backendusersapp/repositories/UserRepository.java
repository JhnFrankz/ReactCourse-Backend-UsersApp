package com.jhnfrankz.backend.usersapp.backendusersapp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    // ya no es necesario crear los metodos como: save, findById, findAll, deleteById
    // porque CrudRepository ya los tiene

    // Método con palabras clave de Spring Data JPA
    Optional<User> findByUsername(String username);

    // Método con JPQL, es como SQL pero con las entidades de Java
    // el ?1 es el primer parametro que se le pasa al metodo
    @Query("select u from User u where u.username=?1")
    Optional<User> getUserByUsername(String username);

    Page<User> findAll(Pageable pageable);
    //Aqui es User ya que interactuamos y se comunica con la base de datos
    // y UserDto es solo para la comunicacion con el cliente
}
