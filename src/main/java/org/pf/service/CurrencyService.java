package org.pf.service;

import org.pf.service.dto.CurrencyDTO;
import java.util.List;

/**
 * Service Interface for managing Currency.
 */
public interface CurrencyService {

    /**
     * Save a currency.
     *
     * @param currencyDTO the entity to save
     * @return the persisted entity
     */
    CurrencyDTO save(CurrencyDTO currencyDTO);

    /**
     * Get all the currencies.
     *
     * @return the list of entities
     */
    List<CurrencyDTO> findAll();

    /**
     * Get the "id" currency.
     *
     * @param id the id of the entity
     * @return the entity
     */
    CurrencyDTO findOne(Long id);

    /**
     * Delete the "id" currency.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the currency corresponding to the query.
     *
     * @param query the query of the search
     *
     * @return the list of entities
     */
    List<CurrencyDTO> search(String query);

    List<CurrencyDTO> findAllByCurrentUser();

    List<CurrencyDTO> findAllByCurrentUser(String login);

    boolean isDuplicateName(String userLogin, String name);
}
