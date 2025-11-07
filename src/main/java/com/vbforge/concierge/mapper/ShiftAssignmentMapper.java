package com.vbforge.concierge.mapper;

import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.entity.ShiftAssignment;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for ShiftAssignment entity
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShiftAssignmentMapper {

    /**
     * Convert entity to DTO
     */
    @Mapping(source = "concierge.id", target = "conciergeId")
    @Mapping(source = "concierge.name", target = "conciergeName")
    @Mapping(source = "concierge.color", target = "conciergeColor")
    ShiftAssignmentDto toDto(ShiftAssignment entity);

    /**
     * Convert DTO to entity
     * Note: concierge relationship must be set manually in service layer
     */
    @Mapping(target = "concierge", ignore = true)
    ShiftAssignment toEntity(ShiftAssignmentDto dto);

    /**
     * Convert list of entities to list of DTOs
     */
    List<ShiftAssignmentDto> toDtoList(List<ShiftAssignment> entities);

    /**
     * Convert list of DTOs to list of entities
     */
    List<ShiftAssignment> toEntityList(List<ShiftAssignmentDto> dtos);

    /**
     * Update existing entity from DTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "concierge", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(ShiftAssignmentDto dto, @MappingTarget ShiftAssignment entity);

}
