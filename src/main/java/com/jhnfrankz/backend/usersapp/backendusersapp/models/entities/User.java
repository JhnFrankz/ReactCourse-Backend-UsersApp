package com.jhnfrankz.backend.usersapp.backendusersapp.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* esta anotacion es para que no se repita el username, solo funciona cuando se crea la tabla por primera vez */
    /* validamos que el username no este vacio y que tenga entre 4 y 8 caracteres, estas validaciones se hacen en el backend, es decir, en el servidor al momento de recibir la peticion del cliente (frontend), por ejemplo si el usuario no envia el username, el backend le responde con un error 400, si el usuario envia un username con menos de 4 caracteres, el backend le responde con un error 400, si el usuario envia un username con mas de 8 caracteres, el backend le responde con un error 400, si el usuario envia un username que ya existe, el backend le responde con un error 400, si el usuario envia un username valido, el backend le responde con un codigo 201 */
    @NotBlank
    @Size(min = 4, max = 8)
    @Column(unique = true)
    private String username;
    
    @NotBlank
    private String password;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @ManyToMany
    private List<Role> roles;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
