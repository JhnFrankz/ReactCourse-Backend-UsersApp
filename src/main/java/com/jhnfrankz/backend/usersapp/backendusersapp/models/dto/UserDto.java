package com.jhnfrankz.backend.usersapp.backendusersapp.models.dto;

// el dto es el objeto (JSON) que se envia en la respuesta al cliente
// el request es el objeto que envia el cliente en la peticion al servidor
// el entity es la persistencia(los datos) en la base de datos
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private boolean admin;

    public UserDto() {
    }

    public UserDto(Long id, String username, String email, boolean admin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.admin = admin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
