package com.spring.security.web.utility.mapper;

public interface Mapper <D, T> {

    /**
     * Converts a domain object to a DTO.
     *
     * @param domain the domain object
     * @return the corresponding DTO
     */
    T toDto(D domain);

    /**
     * Converts a DTO to a domain object.
     *
     * @param dto the DTO
     * @return the corresponding domain object
     */
    D toDomain(T dto);
}
