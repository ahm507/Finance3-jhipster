package org.pf.web.rest;

import org.pf.FinanceApp;

import org.pf.domain.Charts;
import org.pf.repository.ChartsRepository;
import org.pf.service.ChartsService;
import org.pf.repository.search.ChartsSearchRepository;
import org.pf.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.pf.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ChartsResource REST controller.
 *
 * @see ChartsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FinanceApp.class)
public class ChartsResourceIntTest {

    @Autowired
    private ChartsRepository chartsRepository;

    @Autowired
    private ChartsService chartsService;

    @Autowired
    private ChartsSearchRepository chartsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restChartsMockMvc;

    private Charts charts;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChartsResource chartsResource = new ChartsResource(chartsService);
        this.restChartsMockMvc = MockMvcBuilders.standaloneSetup(chartsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Charts createEntity(EntityManager em) {
        Charts charts = new Charts();
        return charts;
    }

    @Before
    public void initTest() {
        chartsSearchRepository.deleteAll();
        charts = createEntity(em);
    }

    @Test
    @Transactional
    public void createCharts() throws Exception {
        int databaseSizeBeforeCreate = chartsRepository.findAll().size();

        // Create the Charts
        restChartsMockMvc.perform(post("/api/charts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(charts)))
            .andExpect(status().isCreated());

        // Validate the Charts in the database
        List<Charts> chartsList = chartsRepository.findAll();
        assertThat(chartsList).hasSize(databaseSizeBeforeCreate + 1);
        Charts testCharts = chartsList.get(chartsList.size() - 1);

        // Validate the Charts in Elasticsearch
        Charts chartsEs = chartsSearchRepository.findOne(testCharts.getId());
        assertThat(chartsEs).isEqualToComparingFieldByField(testCharts);
    }

    @Test
    @Transactional
    public void createChartsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = chartsRepository.findAll().size();

        // Create the Charts with an existing ID
        charts.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChartsMockMvc.perform(post("/api/charts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(charts)))
            .andExpect(status().isBadRequest());

        // Validate the Charts in the database
        List<Charts> chartsList = chartsRepository.findAll();
        assertThat(chartsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCharts() throws Exception {
        // Initialize the database
        chartsRepository.saveAndFlush(charts);

        // Get all the chartsList
        restChartsMockMvc.perform(get("/api/charts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(charts.getId().intValue())));
    }

    @Test
    @Transactional
    public void getCharts() throws Exception {
        // Initialize the database
        chartsRepository.saveAndFlush(charts);

        // Get the charts
        restChartsMockMvc.perform(get("/api/charts/{id}", charts.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(charts.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCharts() throws Exception {
        // Get the charts
        restChartsMockMvc.perform(get("/api/charts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCharts() throws Exception {
        // Initialize the database
        chartsService.save(charts);

        int databaseSizeBeforeUpdate = chartsRepository.findAll().size();

        // Update the charts
        Charts updatedCharts = chartsRepository.findOne(charts.getId());
        // Disconnect from session so that the updates on updatedCharts are not directly saved in db
        em.detach(updatedCharts);

        restChartsMockMvc.perform(put("/api/charts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCharts)))
            .andExpect(status().isOk());

        // Validate the Charts in the database
        List<Charts> chartsList = chartsRepository.findAll();
        assertThat(chartsList).hasSize(databaseSizeBeforeUpdate);
        Charts testCharts = chartsList.get(chartsList.size() - 1);

        // Validate the Charts in Elasticsearch
        Charts chartsEs = chartsSearchRepository.findOne(testCharts.getId());
        assertThat(chartsEs).isEqualToComparingFieldByField(testCharts);
    }

    @Test
    @Transactional
    public void updateNonExistingCharts() throws Exception {
        int databaseSizeBeforeUpdate = chartsRepository.findAll().size();

        // Create the Charts

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restChartsMockMvc.perform(put("/api/charts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(charts)))
            .andExpect(status().isCreated());

        // Validate the Charts in the database
        List<Charts> chartsList = chartsRepository.findAll();
        assertThat(chartsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteCharts() throws Exception {
        // Initialize the database
        chartsService.save(charts);

        int databaseSizeBeforeDelete = chartsRepository.findAll().size();

        // Get the charts
        restChartsMockMvc.perform(delete("/api/charts/{id}", charts.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean chartsExistsInEs = chartsSearchRepository.exists(charts.getId());
        assertThat(chartsExistsInEs).isFalse();

        // Validate the database is empty
        List<Charts> chartsList = chartsRepository.findAll();
        assertThat(chartsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchCharts() throws Exception {
        // Initialize the database
        chartsService.save(charts);

        // Search the charts
        restChartsMockMvc.perform(get("/api/_search/charts?query=id:" + charts.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(charts.getId().intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Charts.class);
        Charts charts1 = new Charts();
        charts1.setId(1L);
        Charts charts2 = new Charts();
        charts2.setId(charts1.getId());
        assertThat(charts1).isEqualTo(charts2);
        charts2.setId(2L);
        assertThat(charts1).isNotEqualTo(charts2);
        charts1.setId(null);
        assertThat(charts1).isNotEqualTo(charts2);
    }
}
