package org.pf.service.impl;

import org.pf.domain.UserSettings;
import org.pf.repository.UserSettingsRepository;
import org.pf.service.UserSettingsService;
import org.pf.service.dto.UserSettingsDTO;
import org.pf.service.mapper.UserSettingsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing UserSettings.
 */
@Service
@Transactional
public class UserSettingsServiceImpl implements UserSettingsService{

    private final Logger log = LoggerFactory.getLogger(UserSettingsServiceImpl.class);

    private final UserSettingsRepository userSettingsRepository;

    private final UserSettingsMapper userSettingsMapper;

    public UserSettingsServiceImpl(UserSettingsRepository userSettingsRepository,
        UserSettingsMapper userSettingsMapper) {
        this.userSettingsRepository = userSettingsRepository;
        this.userSettingsMapper = userSettingsMapper;
    }

    /**
     * Save a userSettings.
     *
     * @param userSettingsDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public UserSettingsDTO save(UserSettingsDTO userSettingsDTO) {
        log.debug("Request to save UserSettings : {}", userSettingsDTO);
        UserSettings userSettings = userSettingsMapper.toEntity(userSettingsDTO);
        userSettings = userSettingsRepository.save(userSettings);
        return userSettingsMapper.toDto(userSettings);
    }

    /**
     * Get all the userSettings.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserSettingsDTO> findAll() {
        log.debug("Request to get all UserSettings");
        return userSettingsRepository.findAll().stream()
            .map(userSettingsMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one userSettings by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public UserSettingsDTO findOne(Long id) {
        log.debug("Request to get UserSettings : {}", id);
        UserSettings userSettings = userSettingsRepository.findOne(id);
        return userSettingsMapper.toDto(userSettings);
    }

    /**
     * Delete the userSettings by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserSettings : {}", id);
        userSettingsRepository.delete(id);
    }

}
