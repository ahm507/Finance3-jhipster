package org.pf.service;

import org.pf.domain.Charts;
import org.pf.repository.ChartsRepository;
import org.pf.repository.search.ChartsSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Charts.
 */
@Service
@Transactional
public class ChartsService {

    private final Logger log = LoggerFactory.getLogger(ChartsService.class);

    private final ChartsRepository chartsRepository;

    private final ChartsSearchRepository chartsSearchRepository;

    public ChartsService(ChartsRepository chartsRepository, ChartsSearchRepository chartsSearchRepository) {
        this.chartsRepository = chartsRepository;
        this.chartsSearchRepository = chartsSearchRepository;
    }

    /**
     * Save a charts.
     *
     * @param charts the entity to save
     * @return the persisted entity
     */
    public Charts save(Charts charts) {
        log.debug("Request to save Charts : {}", charts);
        Charts result = chartsRepository.save(charts);
        chartsSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the charts.
     *
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Charts> findAll() {
        log.debug("Request to get all Charts");
        return chartsRepository.findAll();
    }

    /**
     * Get one charts by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Transactional(readOnly = true)
    public Charts findOne(Long id) {
        log.debug("Request to get Charts : {}", id);
        return chartsRepository.findOne(id);
    }

    /**
     * Delete the charts by id.
     *
     * @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Charts : {}", id);
        chartsRepository.delete(id);
        chartsSearchRepository.delete(id);
    }

    /**
     * Search for the charts corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<Charts> search(String query) {
        log.debug("Request to search Charts for query {}", query);
        return StreamSupport
            .stream(chartsSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
