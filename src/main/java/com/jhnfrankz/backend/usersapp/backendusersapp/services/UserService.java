package com.jhnfrankz.backend.usersapp.backendusersapp.services;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.request.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> findAll();

    Page<UserDto> findAll(Pageable pageable);

    Optional<UserDto> findById(Long id);

    UserDto save(User user);

    Optional<UserDto> update(UserRequest user, Long id);

    void remove(Long id);
    // UserDto es solo para la comunicacion con el cliente
}
