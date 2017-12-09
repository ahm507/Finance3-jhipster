package org.pf.web.rest;

import org.pf.FinanceApp;

import org.pf.domain.UserSettings;
import org.pf.domain.User;
import org.pf.repository.UserSettingsRepository;
import org.pf.service.UserSettingsService;
import org.pf.repository.search.UserSettingsSearchRepository;
import org.pf.service.dto.UserSettingsDTO;
import org.pf.service.mapper.UserSettingsMapper;
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
 * Test class for the UserSettingsResource REST controller.
 *
 * @see UserSettingsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FinanceApp.class)
public class UserSettingsResourceIntTest {

    private static final Double DEFAULT_USD_RATE = 1D;
    private static final Double UPDATED_USD_RATE = 2D;

    private static final Double DEFAULT_SAR_RATE = 1D;
    private static final Double UPDATED_SAR_RATE = 2D;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserSettingsMapper userSettingsMapper;

    @Autowired
    private UserSettingsService userSettingsService;

    @Autowired
    private UserSettingsSearchRepository userSettingsSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restUserSettingsMockMvc;

    private UserSettings userSettings;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserSettingsResource userSettingsResource = new UserSettingsResource(userSettingsService);
        this.restUserSettingsMockMvc = MockMvcBuilders.standaloneSetup(userSettingsResource)
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
    public static UserSettings createEntity(EntityManager em) {
        UserSettings userSettings = new UserSettings()
            .usdRate(DEFAULT_USD_RATE)
            .sarRate(DEFAULT_SAR_RATE);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        userSettings.setUser(user);
        return userSettings;
    }

    @Before
    public void initTest() {
        userSettingsSearchRepository.deleteAll();
        userSettings = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserSettings() throws Exception {
        int databaseSizeBeforeCreate = userSettingsRepository.findAll().size();

        // Create the UserSettings
        UserSettingsDTO userSettingsDTO = userSettingsMapper.toDto(userSettings);
        restUserSettingsMockMvc.perform(post("/api/user-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSettingsDTO)))
            .andExpect(status().isCreated());

        // Validate the UserSettings in the database
        List<UserSettings> userSettingsList = userSettingsRepository.findAll();
        assertThat(userSettingsList).hasSize(databaseSizeBeforeCreate + 1);
        UserSettings testUserSettings = userSettingsList.get(userSettingsList.size() - 1);
        assertThat(testUserSettings.getUsdRate()).isEqualTo(DEFAULT_USD_RATE);
        assertThat(testUserSettings.getSarRate()).isEqualTo(DEFAULT_SAR_RATE);

        // Validate the UserSettings in Elasticsearch
        UserSettings userSettingsEs = userSettingsSearchRepository.findOne(testUserSettings.getId());
        assertThat(userSettingsEs).isEqualToComparingFieldByField(testUserSettings);
    }

    @Test
    @Transactional
    public void createUserSettingsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userSettingsRepository.findAll().size();

        // Create the UserSettings with an existing ID
        userSettings.setId(1L);
        UserSettingsDTO userSettingsDTO = userSettingsMapper.toDto(userSettings);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserSettingsMockMvc.perform(post("/api/user-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSettingsDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserSettings in the database
        List<UserSettings> userSettingsList = userSettingsRepository.findAll();
        assertThat(userSettingsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkUsdRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = userSettingsRepository.findAll().size();
        // set the field null
        userSettings.setUsdRate(null);

        // Create the UserSettings, which fails.
        UserSettingsDTO userSettingsDTO = userSettingsMapper.toDto(userSettings);

        restUserSettingsMockMvc.perform(post("/api/user-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSettingsDTO)))
            .andExpect(status().isBadRequest());

        List<UserSettings> userSettingsList = userSettingsRepository.findAll();
        assertThat(userSettingsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSarRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = userSettingsRepository.findAll().size();
        // set the field null
        userSettings.setSarRate(null);

        // Create the UserSettings, which fails.
        UserSettingsDTO userSettingsDTO = userSettingsMapper.toDto(userSettings);

        restUserSettingsMockMvc.perform(post("/api/user-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSettingsDTO)))
            .andExpect(status().isBadRequest());

        List<UserSettings> userSettingsList = userSettingsRepository.findAll();
        assertThat(userSettingsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUserSettings() throws Exception {
        // Initialize the database
        userSettingsRepository.saveAndFlush(userSettings);

        // Get all the userSettingsList
        restUserSettingsMockMvc.perform(get("/api/user-settings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSettings.getId().intValue())))
            .andExpect(jsonPath("$.[*].usdRate").value(hasItem(DEFAULT_USD_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].sarRate").value(hasItem(DEFAULT_SAR_RATE.doubleValue())));
    }

    @Test
    @Transactional
    public void getUserSettings() throws Exception {
        // Initialize the database
        userSettingsRepository.saveAndFlush(userSettings);

        // Get the userSettings
        restUserSettingsMockMvc.perform(get("/api/user-settings/{id}", userSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(userSettings.getId().intValue()))
            .andExpect(jsonPath("$.usdRate").value(DEFAULT_USD_RATE.doubleValue()))
            .andExpect(jsonPath("$.sarRate").value(DEFAULT_SAR_RATE.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingUserSettings() throws Exception {
        // Get the userSettings
        restUserSettingsMockMvc.perform(get("/api/user-settings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserSettings() throws Exception {
        // Initialize the database
        userSettingsRepository.saveAndFlush(userSettings);
        userSettingsSearchRepository.save(userSettings);
        int databaseSizeBeforeUpdate = userSettingsRepository.findAll().size();

        // Update the userSettings
        UserSettings updatedUserSettings = userSettingsRepository.findOne(userSettings.getId());
        // Disconnect from session so that the updates on updatedUserSettings are not directly saved in db
        em.detach(updatedUserSettings);
        updatedUserSettings
            .usdRate(UPDATED_USD_RATE)
            .sarRate(UPDATED_SAR_RATE);
        UserSettingsDTO userSettingsDTO = userSettingsMapper.toDto(updatedUserSettings);

        restUserSettingsMockMvc.perform(put("/api/user-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSettingsDTO)))
            .andExpect(status().isOk());

        // Validate the UserSettings in the database
        List<UserSettings> userSettingsList = userSettingsRepository.findAll();
        assertThat(userSettingsList).hasSize(databaseSizeBeforeUpdate);
        UserSettings testUserSettings = userSettingsList.get(userSettingsList.size() - 1);
        assertThat(testUserSettings.getUsdRate()).isEqualTo(UPDATED_USD_RATE);
        assertThat(testUserSettings.getSarRate()).isEqualTo(UPDATED_SAR_RATE);

        // Validate the UserSettings in Elasticsearch
        UserSettings userSettingsEs = userSettingsSearchRepository.findOne(testUserSettings.getId());
        assertThat(userSettingsEs).isEqualToComparingFieldByField(testUserSettings);
    }

    @Test
    @Transactional
    public void updateNonExistingUserSettings() throws Exception {
        int databaseSizeBeforeUpdate = userSettingsRepository.findAll().size();

        // Create the UserSettings
        UserSettingsDTO userSettingsDTO = userSettingsMapper.toDto(userSettings);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserSettingsMockMvc.perform(put("/api/user-settings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(userSettingsDTO)))
            .andExpect(status().isCreated());

        // Validate the UserSettings in the database
        List<UserSettings> userSettingsList = userSettingsRepository.findAll();
        assertThat(userSettingsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteUserSettings() throws Exception {
        // Initialize the database
        userSettingsRepository.saveAndFlush(userSettings);
        userSettingsSearchRepository.save(userSettings);
        int databaseSizeBeforeDelete = userSettingsRepository.findAll().size();

        // Get the userSettings
        restUserSettingsMockMvc.perform(delete("/api/user-settings/{id}", userSettings.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean userSettingsExistsInEs = userSettingsSearchRepository.exists(userSettings.getId());
        assertThat(userSettingsExistsInEs).isFalse();

        // Validate the database is empty
        List<UserSettings> userSettingsList = userSettingsRepository.findAll();
        assertThat(userSettingsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchUserSettings() throws Exception {
        // Initialize the database
        userSettingsRepository.saveAndFlush(userSettings);
        userSettingsSearchRepository.save(userSettings);

        // Search the userSettings
        restUserSettingsMockMvc.perform(get("/api/_search/user-settings?query=id:" + userSettings.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userSettings.getId().intValue())))
            .andExpect(jsonPath("$.[*].usdRate").value(hasItem(DEFAULT_USD_RATE.doubleValue())))
            .andExpect(jsonPath("$.[*].sarRate").value(hasItem(DEFAULT_SAR_RATE.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSettings.class);
        UserSettings userSettings1 = new UserSettings();
        userSettings1.setId(1L);
        UserSettings userSettings2 = new UserSettings();
        userSettings2.setId(userSettings1.getId());
        assertThat(userSettings1).isEqualTo(userSettings2);
        userSettings2.setId(2L);
        assertThat(userSettings1).isNotEqualTo(userSettings2);
        userSettings1.setId(null);
        assertThat(userSettings1).isNotEqualTo(userSettings2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserSettingsDTO.class);
        UserSettingsDTO userSettingsDTO1 = new UserSettingsDTO();
        userSettingsDTO1.setId(1L);
        UserSettingsDTO userSettingsDTO2 = new UserSettingsDTO();
        assertThat(userSettingsDTO1).isNotEqualTo(userSettingsDTO2);
        userSettingsDTO2.setId(userSettingsDTO1.getId());
        assertThat(userSettingsDTO1).isEqualTo(userSettingsDTO2);
        userSettingsDTO2.setId(2L);
        assertThat(userSettingsDTO1).isNotEqualTo(userSettingsDTO2);
        userSettingsDTO1.setId(null);
        assertThat(userSettingsDTO1).isNotEqualTo(userSettingsDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userSettingsMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userSettingsMapper.fromId(null)).isNull();
    }
}
