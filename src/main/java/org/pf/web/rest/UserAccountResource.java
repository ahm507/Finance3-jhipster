package org.pf.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.pf.service.UserAccountService;
import org.pf.service.dto.UserAccountDTO;
import org.pf.web.rest.errors.BadRequestAlertException;
import org.pf.web.rest.util.HeaderUtil;
import org.pf.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing UserAccount.
 */
@RestController
@RequestMapping("/api")
public class UserAccountResource {

    private final Logger log = LoggerFactory.getLogger(UserAccountResource.class);

    private static final String ENTITY_NAME = "userAccount";

    private final UserAccountService userAccountService;

    public UserAccountResource(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * POST  /user-accounts : Create a new userAccount.
     *
     * @param userAccountDTO the userAccountDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new userAccountDTO, or with status 400 (Bad Request) if the userAccount has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/user-accounts")
    @Timed
    public ResponseEntity<UserAccountDTO> createUserAccount(@Valid @RequestBody UserAccountDTO userAccountDTO) throws URISyntaxException {
        log.debug("REST request to save UserAccount : {}", userAccountDTO);
        if (userAccountDTO.getId() != null) {
            throw new BadRequestAlertException("A new userAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserAccountDTO result = userAccountService.save(userAccountDTO);
        return ResponseEntity.created(new URI("/api/user-accounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /user-accounts : Updates an existing userAccount.
     *
     * @param userAccountDTO the userAccountDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated userAccountDTO,
     * or with status 400 (Bad Request) if the userAccountDTO is not valid,
     * or with status 500 (Internal Server Error) if the userAccountDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/user-accounts")
    @Timed
    public ResponseEntity<UserAccountDTO> updateUserAccount(@Valid @RequestBody UserAccountDTO userAccountDTO) throws URISyntaxException {
        log.debug("REST request to update UserAccount : {}", userAccountDTO);
        if (userAccountDTO.getId() == null) {
            return createUserAccount(userAccountDTO);
        }
        UserAccountDTO result = userAccountService.save(userAccountDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, userAccountDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /user-accounts : get all the userAccounts.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of userAccounts in body
     */
    @GetMapping("/user-accounts")
    @Timed
    public ResponseEntity<List<UserAccountDTO>> getAllUserAccounts(Pageable pageable) {
        log.debug("REST request to get a page of UserAccounts");
        //Page<UserAccountDTO> page = userAccountService.findAll(pageable);
        Page<UserAccountDTO> page = userAccountService.findByCurrentUser(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/user-accounts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /user-accounts/:id : get the "id" userAccount.
     *
     * @param id the id of the userAccountDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the userAccountDTO, or with status 404 (Not Found)
     */
    @GetMapping("/user-accounts/{id}")
    @Timed
    public ResponseEntity<UserAccountDTO> getUserAccount(@PathVariable Long id) {
        log.debug("REST request to get UserAccount : {}", id);
        UserAccountDTO userAccountDTO = userAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(userAccountDTO));
    }

    /**
     * DELETE  /user-accounts/:id : delete the "id" userAccount.
     *
     * @param id the id of the userAccountDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/user-accounts/{id}")
    @Timed
    public ResponseEntity<Void> deleteUserAccount(@PathVariable Long id) {
        log.debug("REST request to delete UserAccount : {}", id);
        userAccountService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/user-accounts?query=:query : search for the userAccount corresponding
     * to the query.
     *
     * @param query the query of the userAccount search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/user-accounts")
    @Timed
    public ResponseEntity<List<UserAccountDTO>> searchUserAccounts(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of UserAccounts for query {}", query);
        Page<UserAccountDTO> page = userAccountService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/user-accounts");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
