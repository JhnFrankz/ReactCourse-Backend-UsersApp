package com.jhnfrankz.backend.usersapp.backendusersapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
    // ya no es necesario crear los metodos, ya que se hereda de CrudRepository
}
