package org.pf.service.impl;

import org.pf.domain.User;
import org.pf.domain.UserSettings;
import org.pf.repository.UserRepository;
import org.pf.repository.UserSettingsRepository;
import org.pf.repository.search.UserSettingsSearchRepository;
import org.pf.security.SecurityUtils;
import org.pf.service.UserSettingsService;
import org.pf.service.dto.UserSettingsDTO;
import org.pf.service.mapper.UserSettingsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing UserSettings.
 */
@Service
@Transactional
public class UserSettingsServiceImpl implements UserSettingsService{

    private final Logger log = LoggerFactory.getLogger(UserSettingsServiceImpl.class);

    private final UserSettingsRepository userSettingsRepository;

    private final UserSettingsMapper userSettingsMapper;

    private final UserSettingsSearchRepository userSettingsSearchRepository;

    private final UserRepository userRepository;


    public UserSettingsServiceImpl(UserSettingsRepository userSettingsRepository, UserSettingsMapper userSettingsMapper,
        UserSettingsSearchRepository userSettingsSearchRepository,
        UserRepository userRepository) {
        this.userSettingsRepository = userSettingsRepository;
        this.userSettingsMapper = userSettingsMapper;
        this.userSettingsSearchRepository = userSettingsSearchRepository;
        this.userRepository = userRepository;
    }

    private void enforceSavingToCurrentUser(UserSettingsDTO userSettingsDTO) {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        userSettingsDTO.setUserLogin(login.get());
        Optional<User> user = userRepository.findOneByLogin(login.get());
        userSettingsDTO.setUserId(user.get().getId());
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

        enforceSavingToCurrentUser(userSettingsDTO);

        UserSettings userSettings = userSettingsMapper.toEntity(userSettingsDTO);
        userSettings = userSettingsRepository.save(userSettings);
        UserSettingsDTO result = userSettingsMapper.toDto(userSettings);
        userSettingsSearchRepository.save(userSettings);
        return result;
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

    public List<UserSettingsDTO> findAllByCurrentUser() {
        log.debug("Request to get all UserSettings");
        return userSettingsRepository.findAllByCurrentUser().stream()
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
        userSettingsSearchRepository.delete(id);
    }

    /**
     * Search for the userSettings corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserSettingsDTO> search(String query) {
        log.debug("Request to search UserSettings for query {}", query);
        return StreamSupport
            .stream(userSettingsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(userSettingsMapper::toDto)
            .collect(Collectors.toList());
    }
}
