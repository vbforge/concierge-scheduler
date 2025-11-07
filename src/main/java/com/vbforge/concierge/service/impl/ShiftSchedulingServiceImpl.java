package com.vbforge.concierge.service.impl;

import com.vbforge.concierge.dto.ShiftAssignmentDto;
import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.entity.ShiftAssignment;
import com.vbforge.concierge.exception.ShiftAssignmentNotFoundException;
import com.vbforge.concierge.mapper.ShiftAssignmentMapper;
import com.vbforge.concierge.repository.ConciergeRepository;
import com.vbforge.concierge.repository.ShiftAssignmentRepository;
import com.vbforge.concierge.service.ShiftSchedulingService;
import com.vbforge.concierge.service.ValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ShiftSchedulingService
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShiftSchedulingServiceImpl implements ShiftSchedulingService {

    private final ShiftAssignmentRepository shiftRepository;
    private final ConciergeRepository conciergeRepository;
    private final ShiftAssignmentMapper shiftMapper;
    private final ValidationService validationService;

    @Override
    @Transactional
    public ShiftAssignmentDto assignShift(ShiftAssignmentDto shiftAssignmentDto) {
        log.info("Assigning shift for date: {} to concierge ID: {}",
                shiftAssignmentDto.getShiftDate(), shiftAssignmentDto.getConciergeId());

        //validate shift assignment
        validationService.validateShiftAssignment(shiftAssignmentDto);

        //check for conflicts
        validationService.checkShiftConflict(shiftAssignmentDto.getShiftDate());

        //validate concierge exists
        validationService.validateConciergeExists(shiftAssignmentDto.getConciergeId());

        //create shift assignment
        ShiftAssignment assignment = shiftMapper.toEntity(shiftAssignmentDto);

        //set concierge relationship
        Concierge concierge = conciergeRepository.findById(shiftAssignmentDto.getConciergeId())
                .orElseThrow();

        assignment.setConcierge(concierge);

        ShiftAssignment saved = shiftRepository.save(assignment);

        log.info("Shift assigned with ID: {}", saved.getId());
        return shiftMapper.toDto(saved);

    }

    @Override
    @Transactional
    public ShiftAssignmentDto updateShift(Long id, ShiftAssignmentDto shiftAssignmentDto) {
        log.info("Updating shift ID: {}", id);

        ShiftAssignment existing = findShiftById(id);

        //validate updated data
        validationService.validateShiftAssignment(shiftAssignmentDto);

        //if date is changing, check for conflicts
        if(!existing.getShiftDate().equals(shiftAssignmentDto.getShiftDate())){
            validationService.checkShiftConflict(shiftAssignmentDto.getShiftDate());
        }

        // If concierge is changing, validate and update
        if (!existing.getConcierge().getId().equals(shiftAssignmentDto.getConciergeId())) {
            validationService.validateConciergeExists(shiftAssignmentDto.getConciergeId());
            Concierge newConcierge = conciergeRepository.findById(shiftAssignmentDto.getConciergeId())
                    .orElseThrow();
            existing.setConcierge(newConcierge);
        }

        shiftMapper.updateEntityFromDto(shiftAssignmentDto, existing);
        ShiftAssignment updated = shiftRepository.save(existing);

        log.info("Shift updated: {}", updated.getId());
        return shiftMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void removeShift(Long id) {
        log.info("Removing shift ID: {}", id);

        ShiftAssignment shift = findShiftById(id);
        shift.softDelete();
        shiftRepository.save(shift);

        log.info("Shift soft deleted: {}", id);
    }

    @Override
    @Transactional
    public void removeShiftByDate(LocalDate date) {
        log.info("Removing shift for date: {}", date);

        ShiftAssignment shift = shiftRepository.findByShiftDateAndDeletedFalse(date)
                .orElseThrow(() -> new ShiftAssignmentNotFoundException(date.toString()));

        shift.softDelete();
        shiftRepository.save(shift);

        log.info("Shift removed for date: {}", date);
    }

    @Override
    public ShiftAssignmentDto getShiftById(Long id) {
        log.debug("Getting shift by ID: {}", id);

        ShiftAssignment shift = findShiftById(id);
        return shiftMapper.toDto(shift);
    }

    @Override
    public ShiftAssignmentDto getShiftByDate(LocalDate date) {
        log.debug("Getting shift by date: {}", date);

        return shiftRepository.findByShiftDateAndDeletedFalse(date)
                .map(shiftMapper::toDto)
                .orElse(null);
    }

    @Override
    public List<ShiftAssignmentDto> getShiftsByConcierge(Long conciergeId) {
        log.debug("Getting shifts for concierge ID: {}", conciergeId);

        List<ShiftAssignment> shifts = shiftRepository.findByConciergeIdAndDeletedFalse(conciergeId);
        return shiftMapper.toDtoList(shifts);
    }

    @Override
    public List<ShiftAssignmentDto> getShiftsInDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting shifts from {} to {}", startDate, endDate);

        List<ShiftAssignment> shifts = shiftRepository.findByShiftDateBetweenAndDeletedFalse(startDate, endDate);
        return shiftMapper.toDtoList(shifts);
    }

    @Override
    public boolean isShiftAssigned(LocalDate date) {
        return shiftRepository.existsByShiftDateAndDeletedFalse(date);
    }

    @Override
    @Transactional
    public List<ShiftAssignmentDto> bulkAssignShifts(List<ShiftAssignmentDto> shiftAssignments) {
        log.info("Bulk assigning {} shifts", shiftAssignments.size());

        List<ShiftAssignmentDto> created = new ArrayList<>();

        for (ShiftAssignmentDto dto : shiftAssignments) {
            try {
                ShiftAssignmentDto assigned = assignShift(dto);
                created.add(assigned);
            } catch (Exception e) {
                log.warn("Failed to assign shift for date {}: {}", dto.getShiftDate(), e.getMessage());
                // Continue with remaining shifts
            }
        }

        log.info("Successfully assigned {} out of {} shifts", created.size(), shiftAssignments.size());
        return created;
    }

    @Override
    @Transactional
    public void deleteAllShiftsForConcierge(Long conciergeId) {
        log.info("Deleting all shifts for concierge ID: {}", conciergeId);

        List<ShiftAssignment> shifts = shiftRepository.findByConciergeIdAndDeletedFalse(conciergeId);

        for (ShiftAssignment shift : shifts) {
            shift.softDelete();
        }

        shiftRepository.saveAll(shifts);
        log.info("Deleted {} shifts for concierge ID: {}", shifts.size(), conciergeId);
    }

    //helper method to find shift by id
    private ShiftAssignment findShiftById(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> new ShiftAssignmentNotFoundException(id));
    }
}
