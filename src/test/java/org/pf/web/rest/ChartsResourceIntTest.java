package org.pf.web.rest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.pf.FinanceApp;
import org.pf.domain.Currency;
import org.pf.domain.Transaction;
import org.pf.domain.User;
import org.pf.domain.UserAccount;
import org.pf.domain.enumeration.AccountType;
import org.pf.service.ChartsService;
import org.pf.web.rest.errors.ExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;
import static org.pf.web.rest.TestUtil.*;

/**
 * Test class for the ChartsResource REST controller.
 *
 * @see ChartsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FinanceApp.class)
public class ChartsResourceIntTest {

    @Autowired
    private ChartsService chartsService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restChartsMockMvc;

//    private Charts charts;

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
//    public static Charts createEntity(EntityManager em) {
//
//        return null;
//    }
    User user;

    @Before
    public void initTest() {
        user = createUser();
        em.persist(user);
        em.flush();
        Currency egp = createCurrency(user, "egp", 1.0);
        em.flush();
        user.setMasterCurrency(egp.getId());
        em.persist(user);
        em.flush();
        Currency usd = createCurrency(user, "usd", 20.0);
        em.flush();
        UserAccount asset1 = createUserAccount(user, "asset1", AccountType.ASSET, egp);
        UserAccount income1 = createUserAccount(user, "income1", AccountType.INCOME, egp);
        UserAccount expense1 = createUserAccount(user, "expense1", AccountType.EXPENSE, egp);
        UserAccount liability1 = createUserAccount(user, "liability1", AccountType.LIABILITY, egp);
        UserAccount other1 = createUserAccount(user, "other1", AccountType.OTHER, usd);
        em.flush();

        //TODO: extract into DateUtils

        ZonedDateTime date1 = ZonedDateTime.parse("2017-01-01 01:01:01.0", DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss.S").withZone(ZoneId.systemDefault()));
//        ZonedDateTime date2 = ZonedDateTime.parse("2016-01-01 02:02:02.0", DateTimeFormatter.ofPattern(
//            "yyyy-MM-dd HH:mm:ss.S").withZone(ZoneId.systemDefault()));

        createTransaction(user, date1, 100.0, asset1, income1);
        createTransaction(user, date1, 100.0, asset1, income1);
        createTransaction(user, date1, 100.0, asset1, expense1);
        createTransaction(user, date1, 100.0, asset1, expense1);
        createTransaction(user, date1, 100.0, asset1, expense1);
        createTransaction(user, date1, 100.0, liability1, other1);
        createTransaction(user, date1, 100.0, liability1, other1);
        createTransaction(user, date1, 100.0, other1, liability1);
        em.flush();
    }

    private final String DEFAULT_LOGIN = "test1";

    public User createUser() {
        final String DEFAULT_EMAIL = "test1@localhost";
        final String DEFAULT_FIRSTNAME = "john";
        final String DEFAULT_LASTNAME = "doe";
        final String DEFAULT_IMAGEURL = "http://placehold.it/50x50";
        final String DEFAULT_LANGKEY = "en";
        User user = new User();
        user.setLogin(DEFAULT_LOGIN + RandomStringUtils.randomAlphabetic(5));
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(RandomStringUtils.randomAlphabetic(5) + DEFAULT_EMAIL);
        user.setFirstName(DEFAULT_FIRSTNAME);
        user.setLastName(DEFAULT_LASTNAME);
        user.setImageUrl(DEFAULT_IMAGEURL);
        user.setLangKey(DEFAULT_LANGKEY);
        return user;
    }

    public Currency createCurrency(User user, String name, double conversionRate) {
        Currency currency = new Currency()
            .name(name)
            .conversionRate(conversionRate);
        // Add required entity
//        User user = UserResourceIntTest.createEntity(em);
        currency.setUser(user);
        em.persist(currency);
        return currency;
    }

    public UserAccount createUserAccount(User user, String text, AccountType type, Currency currency) {
        UserAccount userAccount = new UserAccount()
            .text(text)
            .description("")
            .type(type);
        // Add required entity
//        User user = UserResourceIntTest.createEntity(em);
//        em.persist(user);
//        em.flush();
        userAccount.setUser(user);
        // Add required entity
//        Currency currency = CurrencyResourceIntTest.createEntity(em);
//        em.persist(currency);
//        em.flush();
        userAccount.setCurrency(currency);
        em.persist(userAccount);
        return userAccount;
    }

    public Transaction createTransaction(User user, ZonedDateTime date, double amount,
        UserAccount withdrawAccount, UserAccount depositAccount) {
        Transaction transaction = new Transaction()
            .date(date)
            .amount(amount)
            .description(" ");
        // Add required entity
        transaction.setUser(user);
        // Add required entity
//        UserAccount withdrawAccount = UserAccountResourceIntTest.createEntity(em);
//        em.persist(withdrawAccount);
//        em.flush();
        transaction.setWithdrawAccount(withdrawAccount);
        // Add required entity
//        UserAccount depositAccount = UserAccountResourceIntTest.createEntity(em);
//        em.persist(depositAccount);
//        em.flush();
        transaction.setDepositAccount(depositAccount);
        em.persist(transaction);
        return transaction;
    }

    @Test
    @Transactional
    public void charts() throws Exception {

        String html = chartsService.getTransactionsTrendHtml("2017", "ASSET", user.getLogin());
        assertThat(html)
            .isNotEmpty()
            .contains("<tr><td>Feb</td><td>-500.00</td><td>-500.00</td></tr>");

        html = chartsService.getTransactionsTrendHtml("", "", user.getLogin());
        assertThat(html)
            .isNotEmpty()
            .contains("<tr><td>-2,000.00</td><td>2017</td><td>-200.00</td><td>300.00</td><td>100.00</td><td>-500.00</td></tr>");

        html = chartsService.getTransactionsTrendHtml("2017", "EXPENSE", user.getLogin());
        assertThat(html)
            .isNotEmpty()
            .contains("<tr><td>Jan</td><td>300.00</td><td>300.00</td></tr>");


        html = chartsService.getTransactionsTrendHtml("2017", "LIABILITY", user.getLogin());
        assertThat(html)
            .isNotEmpty()
            .contains("<tr><td>Feb</td><td>100.00</td><td>100.00</td></tr>");

        html = chartsService.getTransactionsTrendHtml("2017", "INCOME", user.getLogin());
        assertThat(html)
            .isNotEmpty()
            .contains("<tr><td>Jan</td><td>-200.00</td><td>-200.00</td></tr>");




        //        int databaseSizeBeforeCreate = chartsRepository.findAll().size();
//
//        // Create the Charts
//        restChartsMockMvc.perform(post("/api/charts")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(charts)))
//            .andExpect(status().isCreated());
//
//        // Validate the Charts in the database
//        List<Charts> chartsList = chartsRepository.findAll();
//        assertThat(chartsList).hasSize(databaseSizeBeforeCreate + 1);
//        Charts testCharts = chartsList.get(chartsList.size() - 1);
//
//        // Validate the Charts in Elasticsearch
//        Charts chartsEs = chartsSearchRepository.findOne(testCharts.getId());
//        assertThat(chartsEs).isEqualToComparingFieldByField(testCharts);
    }


}
