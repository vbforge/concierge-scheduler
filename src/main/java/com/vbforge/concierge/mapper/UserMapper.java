package com.vbforge.concierge.mapper;

import com.vbforge.concierge.dto.UserDto;
import com.vbforge.concierge.entity.User;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for User entity
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * Convert entity to DTO
     * Excludes password for security
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "confirmPassword", ignore = true)
    @Mapping(source = "concierge.id", target = "conciergeId")
    @Mapping(source = "concierge.name", target = "conciergeName")
    UserDto toDto(User entity);

    /**
     * Convert DTO to entity
     * Password will be encoded separately in service
     */
    @Mapping(target = "concierge", ignore = true)
    User toEntity(UserDto dto);

    /**
     * Convert list of entities to list of DTOs
     */
    List<UserDto> toDtoList(List<User> entities);

    /**
     * Update existing entity from DTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Updated separately
    @Mapping(target = "concierge", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);

}
