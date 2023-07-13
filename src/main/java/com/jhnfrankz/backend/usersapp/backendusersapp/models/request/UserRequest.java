package com.jhnfrankz.backend.usersapp.backendusersapp.models.request;

import com.jhnfrankz.backend.usersapp.backendusersapp.models.IUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// la clase UserRequest es el objeto que envia el cliente en la peticion al servidor,
public class UserRequest implements IUser {

    @NotBlank
    @Size(min = 4, max = 8)
    private String username;

    @NotBlank
    @Email
    private String email;

    private boolean admin;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
