package org.pf.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.pf.security.SecurityUtils;
import org.pf.service.ChartsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
     * GET  /charts : get all the charts statistics rendered as html.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of charts in body
     */
    @GetMapping("/chartsHtml")
    @Timed
    public String getAllCharts(
        @RequestParam(required = false, value = "login") String login,
        @RequestParam(required = false, value = "type") String type,
        @RequestParam(required = false, value = "year") String year ) throws Exception {

        log.debug("REST request to get all Charts");

        if(login == null) {
            login = SecurityUtils.getCurrentUserLogin().get();
        }

        return chartsService.getTransactionsTrendHtml(year, type, login);

    }




}
