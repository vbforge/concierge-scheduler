package com.vbforge.concierge.service.impl;

import com.vbforge.concierge.service.ConciergeService;

import com.vbforge.concierge.dto.ConciergeDto;
import com.vbforge.concierge.entity.Concierge;
import com.vbforge.concierge.enums.ColorType;
import com.vbforge.concierge.exception.ConciergeNotFoundException;
import com.vbforge.concierge.mapper.ConciergeMapper;
import com.vbforge.concierge.repository.ConciergeRepository;
import com.vbforge.concierge.service.ValidationService;
import com.vbforge.concierge.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of ConciergeService
 * Transactional annotated: create, update, delete, activate & deactivate
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConciergeServiceImpl implements ConciergeService {

    private final ConciergeRepository conciergeRepository;
    private final ConciergeMapper conciergeMapper;
    private final ValidationService validationService;

    @Override
    @Transactional
    public ConciergeDto createConcierge(ConciergeDto conciergeDto) {
        log.info("Creating Concierge: {}", conciergeDto.getName());

        validationService.validateConciergeNameUnique(conciergeDto.getName());

        Concierge concierge = conciergeMapper.toEntity(conciergeDto);
        Concierge saved = conciergeRepository.save(concierge);
        log.info("Concierge created with ID: {}", saved.getId());
        return conciergeMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ConciergeDto updateConcierge(Long id, ConciergeDto conciergeDto) {
        log.info("Updating concierge ID: {}", id);

        Concierge exist = findConciergeById(id);

        //check if name been changed and new name is unique
        if(!exist.getName().equalsIgnoreCase(conciergeDto.getName())){
            validationService.validateConciergeNameUnique(conciergeDto.getName());
        }
        conciergeMapper.updateEntityFromDto(conciergeDto, exist);
        Concierge updated = conciergeRepository.save(exist);

        log.info("Concierge updated: {}", updated.getName());
        return conciergeMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteConcierge(Long id) {
        log.info("Deleting concierge ID: {}", id);

        Concierge concierge = findConciergeById(id);

        // Check if concierge can be deleted
        validationService.validateConciergeCanBeDeleted(id);

        concierge.softDelete();
        conciergeRepository.save(concierge);

        log.info("Concierge soft deleted: {}", concierge.getName());
    }

    @Override
    public ConciergeDto getConciergeById(Long id) {
        log.debug("Getting concierge by ID: {}", id);

        Concierge concierge = findConciergeById(id);
        ConciergeDto dto = conciergeMapper.toDto(concierge);

        // Enrich with shift counts
        dto.setCurrentMonthShiftCount(getCurrentMonthShiftCount(id).intValue());
        dto.setTotalShiftCount(getTotalShiftCount(id).intValue());

        return dto;
    }

    @Override
    public ConciergeDto getConciergeByName(String name) {
        log.debug("Getting concierge by name: {}", name);

        Concierge concierge = conciergeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ConciergeNotFoundException(name));

        return conciergeMapper.toDto(concierge);
    }

    @Override
    public List<ConciergeDto> getAllActiveConcierges() {
        log.debug("Getting all active concierges");

        List<Concierge> concierges = conciergeRepository.findByActiveTrueAndDeletedFalse();
        return conciergeMapper.toDtoList(concierges);
    }

    @Override
    public List<ConciergeDto> getAllConcierges() {
        log.debug("Getting all concierges");

        List<Concierge> concierges = conciergeRepository.findByDeletedFalse();
        return conciergeMapper.toDtoList(concierges);
    }

    @Override
    public List<ConciergeDto> getConciergesByColor(ColorType color) {
        log.debug("Getting concierges by color: {}", color);

        List<Concierge> concierges = conciergeRepository.findByColor(color);
        return conciergeMapper.toDtoList(concierges);
    }

    @Override
    @Transactional
    public void activateConcierge(Long id) {
        log.info("Activating Concierge ID: {}", id);
        Concierge concierge = findConciergeById(id);
        concierge.activate();
        conciergeRepository.save(concierge);

        log.info("Concierge Activated: {}", concierge.getName());
    }

    @Override
    public void deactivateConcierge(Long id) {
        log.info("Deactivating Concierge ID: {}", id);
        Concierge concierge = findConciergeById(id);
        concierge.deactivate();
        conciergeRepository.save(concierge);

        log.info("Concierge Deactivated: {}", concierge.getName());
    }

    @Override
    public boolean existsByName(String name) {
        return conciergeRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public Long getCurrentMonthShiftCount(Long conciergeId) {
        int year = DateUtils.getCurrentYear();
        int month = DateUtils.getCurrentMonth();
        return conciergeRepository.countShiftsByMonth(conciergeId, year, month);
    }

    @Override
    public Long getTotalShiftCount(Long conciergeId) {
        return conciergeRepository.countTotalShifts(conciergeId);
    }

    //helper method to find Concierge by id (return Optional<Concierge>)
    private Concierge findConciergeById(Long id){
        return conciergeRepository.findById(id)
                .orElseThrow(() -> new ConciergeNotFoundException(id));
    }

}
