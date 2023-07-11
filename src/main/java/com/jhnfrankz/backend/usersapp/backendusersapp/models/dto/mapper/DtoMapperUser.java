package com.jhnfrankz.backend.usersapp.backendusersapp.models.dto.mapper;

public class DtoMapperUser {
    // Patrón de diseño builder

    private static DtoMapperUser mapper;

    private DtoMapperUser() {
    }

    public static DtoMapperUser getInstance() {
        mapper = new DtoMapperUser();
        return mapper;
    }
}
