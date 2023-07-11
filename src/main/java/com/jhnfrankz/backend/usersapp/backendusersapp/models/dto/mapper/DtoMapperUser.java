package com.jhnfrankz.backend.usersapp.backendusersapp.models.dto.mapper;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.jhnfrankz.backend.usersapp.backendusersapp.models.entities.User;

public class DtoMapperUser {
    // Patrón de diseño builder

    private User user;

    private DtoMapperUser() {
    }

    public static DtoMapperUser builder() {
        // devolvemos una instancia única cada vez que se llame a este método y no es compartida
        return new DtoMapperUser();
    }

    public DtoMapperUser setUser(User user) {
        this.user = user;
        return this; // devolvemos la instancia de la clase para volver a llamar otros métodos
    }

    public UserDto build() {
        if (user == null) {
            throw new RuntimeException("Debe pasar el entity user");
        }

        return new UserDto(this.user.getId(), this.user.getUsername(), this.user.getEmail());
    }

}
