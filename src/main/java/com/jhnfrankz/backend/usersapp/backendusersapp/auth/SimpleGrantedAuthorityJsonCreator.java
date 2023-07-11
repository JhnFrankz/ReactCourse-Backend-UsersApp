package com.jhnfrankz.backend.usersapp.backendusersapp.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class SimpleGrantedAuthorityJsonCreator {

    // debe tener el mismo constructor que la clase SimpleGrantedAuthority
    // este constructor es usado por el ObjectMapper para convertir el json a un objeto SimpleGrantedAuthority
    // JsonProperty("authority") es el nombre del campo en el json que se convertira a la propiedad authority
    // en el objeto SimpleGrantedAuthority
    // Le decimos que el rol lo pase de la propiedad del JSON llamada "authority"
    @JsonCreator
    public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {
    }
}
