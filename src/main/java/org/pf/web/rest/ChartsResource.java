package org.pf.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.pf.domain.Charts;
import org.pf.service.ChartsService;
import org.pf.web.rest.errors.BadRequestAlertException;
import org.pf.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Charts.
 */
@RestController
@RequestMapping("/api")
public class ChartsResource {

    private final Logger log = LoggerFactory.getLogger(ChartsResource.class);

    private static final String ENTITY_NAME = "charts";

    private final ChartsService chartsService;

    public ChartsResource(ChartsService chartsService) {
        this.chartsService = chartsService;
    }

    /**
     * POST  /charts : Create a new charts.
     *
     * @param charts the charts to create
     * @return the ResponseEntity with status 201 (Created) and with body the new charts, or with status 400 (Bad Request) if the charts has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/charts")
    @Timed
    public ResponseEntity<Charts> createCharts(@RequestBody Charts charts) throws URISyntaxException {
        log.debug("REST request to save Charts : {}", charts);
        if (charts.getId() != null) {
            throw new BadRequestAlertException("A new charts cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Charts result = chartsService.save(charts);
        return ResponseEntity.created(new URI("/api/charts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /charts : Updates an existing charts.
     *
     * @param charts the charts to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated charts,
     * or with status 400 (Bad Request) if the charts is not valid,
     * or with status 500 (Internal Server Error) if the charts couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/charts")
    @Timed
    public ResponseEntity<Charts> updateCharts(@RequestBody Charts charts) throws URISyntaxException {
        log.debug("REST request to update Charts : {}", charts);
        if (charts.getId() == null) {
            return createCharts(charts);
        }
        Charts result = chartsService.save(charts);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, charts.getId().toString()))
            .body(result);
    }

    /**
     * GET  /charts : get all the charts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of charts in body
     */
    @GetMapping("/charts")
    @Timed
    public List<Charts> getAllCharts() {
        log.debug("REST request to get all Charts");
        return chartsService.findAll();
        }

    /**
     * GET  /charts/:id : get the "id" charts.
     *
     * @param id the id of the charts to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the charts, or with status 404 (Not Found)
     */
    @GetMapping("/charts/{id}")
    @Timed
    public ResponseEntity<Charts> getCharts(@PathVariable Long id) {
        log.debug("REST request to get Charts : {}", id);
        Charts charts = chartsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(charts));
    }

    /**
     * DELETE  /charts/:id : delete the "id" charts.
     *
     * @param id the id of the charts to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/charts/{id}")
    @Timed
    public ResponseEntity<Void> deleteCharts(@PathVariable Long id) {
        log.debug("REST request to delete Charts : {}", id);
        chartsService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/charts?query=:query : search for the charts corresponding
     * to the query.
     *
     * @param query the query of the charts search
     * @return the result of the search
     */
    @GetMapping("/_search/charts")
    @Timed
    public List<Charts> searchCharts(@RequestParam String query) {
        log.debug("REST request to search Charts for query {}", query);
        return chartsService.search(query);
    }

}
