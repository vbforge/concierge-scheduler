package com.vbforge.concierge.mapper;

import com.vbforge.concierge.dto.MonthHistoryDto;
import com.vbforge.concierge.entity.MonthHistory;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for MonthHistory entity
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MonthHistoryMapper {

    /**
     * Convert entity to DTO
     */
    MonthHistoryDto toDto(MonthHistory entity);

    /**
     * Convert DTO to entity
     */
    MonthHistory toEntity(MonthHistoryDto dto);

    /**
     * Convert list of entities to list of DTOs
     */
    List<MonthHistoryDto> toDtoList(List<MonthHistory> entities);

    /**
     * Convert list of DTOs to list of entities
     */
    List<MonthHistory> toEntityList(List<MonthHistoryDto> dtos);

    /**
     * Update existing entity from DTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDto(MonthHistoryDto dto, @MappingTarget MonthHistory entity);

}
