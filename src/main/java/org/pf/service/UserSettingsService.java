package org.pf.service;

import org.pf.service.dto.UserSettingsDTO;
import java.util.List;

/**
 * Service Interface for managing UserSettings.
 */
public interface UserSettingsService {

    /**
     * Save a userSettings.
     *
     * @param userSettingsDTO the entity to save
     * @return the persisted entity
     */
    UserSettingsDTO save(UserSettingsDTO userSettingsDTO);

    /**
     * Get all the userSettings.
     *
     * @return the list of entities
     */
    List<UserSettingsDTO> findAll();

    /**
     * Get the "id" userSettings.
     *
     * @param id the id of the entity
     * @return the entity
     */
    UserSettingsDTO findOne(Long id);

    /**
     * Delete the "id" userSettings.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

}
