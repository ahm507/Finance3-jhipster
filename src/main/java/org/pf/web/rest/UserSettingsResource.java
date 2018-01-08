package org.pf.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.pf.service.UserSettingsService;
import org.pf.service.dto.UserSettingsDTO;
import org.pf.web.rest.errors.BadRequestAlertException;
import org.pf.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing UserSettings.
 */
@RestController
@RequestMapping("/api")
public class UserSettingsResource {

    private final Logger log = LoggerFactory.getLogger(UserSettingsResource.class);

    private static final String ENTITY_NAME = "userSettings";

    private final UserSettingsService userSettingsService;

    public UserSettingsResource(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    /**
     * POST  /user-settings : Create a new userSettings.
     *
     * @param userSettingsDTO the userSettingsDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userSettingsDTO, or with status 400 (Bad Request) if the userSettings has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-settings")
    @Timed
    public ResponseEntity<UserSettingsDTO> createUserSettings(@RequestBody UserSettingsDTO userSettingsDTO) throws URISyntaxException {
        log.debug("REST request to save UserSettings : {}", userSettingsDTO);
        if (userSettingsDTO.getId() != null) {
            throw new BadRequestAlertException("A new userSettings cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserSettingsDTO result = userSettingsService.save(userSettingsDTO);
        return ResponseEntity.created(new URI("/api/user-settings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-settings : Updates an existing userSettings.
     *
     * @param userSettingsDTO the userSettingsDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userSettingsDTO,
     * or with status 400 (Bad Request) if the userSettingsDTO is not valid,
     * or with status 500 (Internal Server Error) if the userSettingsDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-settings")
    @Timed
    public ResponseEntity<UserSettingsDTO> updateUserSettings(@RequestBody UserSettingsDTO userSettingsDTO) throws URISyntaxException {
        log.debug("REST request to update UserSettings : {}", userSettingsDTO);
        if (userSettingsDTO.getId() == null) {
            return createUserSettings(userSettingsDTO);
        }
        UserSettingsDTO result = userSettingsService.save(userSettingsDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userSettingsDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-settings : get all the userSettings.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of userSettings in body
     */
    @GetMapping("/user-settings")
    @Timed
    public List<UserSettingsDTO> getAllUserSettings() {
        log.debug("REST request to get all UserSettings");
        return userSettingsService.findAll();
        }

    /**
     * GET  /user-settings/:id : get the "id" userSettings.
     *
     * @param id the id of the userSettingsDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userSettingsDTO, or with status 404 (Not Found)
     */
    @GetMapping("/user-settings/{id}")
    @Timed
    public ResponseEntity<UserSettingsDTO> getUserSettings(@PathVariable Long id) {
        log.debug("REST request to get UserSettings : {}", id);
        UserSettingsDTO userSettingsDTO = userSettingsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(userSettingsDTO));
    }

    /**
     * DELETE  /user-settings/:id : delete the "id" userSettings.
     *
     * @param id the id of the userSettingsDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-settings/{id}")
    @Timed
    public ResponseEntity<Void> deleteUserSettings(@PathVariable Long id) {
        log.debug("REST request to delete UserSettings : {}", id);
        userSettingsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
