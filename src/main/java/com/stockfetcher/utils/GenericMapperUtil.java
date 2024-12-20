package com.stockfetcher.utils;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;


public class GenericMapperUtil {

    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        // Configure ModelMapper for strict matching
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * Convert an entity to a DTO.
     *
     * @param <D>      The DTO type.
     * @param <E>      The Entity type.
     * @param entity   The entity to convert.
     * @param dtoClass The DTO class type.
     * @return The converted DTO.
     */
    public static <D, E> D convertToDto(E entity, Class<D> dtoClass) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, dtoClass);
    }

    /**
     * Convert a DTO to an entity.
     *
     * @param <D>        The DTO type.
     * @param <E>        The Entity type.
     * @param dto        The DTO to convert.
     * @param entityClass The Entity class type.
     * @return The converted Entity.
     */
    public static <D, E> E convertToEntity(D dto, Class<E> entityClass) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, entityClass);
    }

    /**
     * Convert a list of entities to a list of DTOs.
     *
     * @param <D>       The DTO type.
     * @param <E>       The Entity type.
     * @param entities  The list of entities to convert.
     * @param dtoClass  The DTO class type.
     * @return A list of converted DTOs.
     */
    public static <D, E> List<D> convertToDtoList(List<E> entities, Class<D> dtoClass) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(entity -> convertToDto(entity, dtoClass))
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of DTOs to a list of entities.
     *
     * @param <D>        The DTO type.
     * @param <E>        The Entity type.
     * @param dtos       The list of DTOs to convert.
     * @param entityClass The Entity class type.
     * @return A list of converted Entities.
     */
    public static <D, E> List<E> convertToEntityList(List<D> dtos, Class<E> entityClass) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        return dtos.stream()
                .map(dto -> convertToEntity(dto, entityClass))
                .collect(Collectors.toList());
    }
}
