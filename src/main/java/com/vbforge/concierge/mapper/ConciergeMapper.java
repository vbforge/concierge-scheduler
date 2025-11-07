package com.vbforge.concierge.mapper;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.entity.Concierge;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Concierge entity
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConciergeMapper {

    /**
     * Convert entity to DTO
     */
    @Mapping(target = "currentMonthShiftCount", ignore = true)
    @Mapping(target = "totalShiftCount", ignore = true)
    ConciergeDto toDto(Concierge entity);

    /**
     * Convert DTO to entity
     */
    @Mapping(target = "shiftAssignments", ignore = true)
    Concierge toEntity(ConciergeDto dto);

    /**
     * Convert list of entities to list of DTOs
     */
    List<ConciergeDto> toDtoList(List<Concierge> entities);

    /**
     * Convert list of DTOs to list of entities
     */
    List<Concierge> toEntityList(List<ConciergeDto> dtos);

    /**
     * Update existing entity from DTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shiftAssignments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(ConciergeDto dto, @MappingTarget Concierge entity);

}
