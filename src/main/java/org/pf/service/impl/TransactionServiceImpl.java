package org.pf.service.impl;

import org.pf.domain.Transaction;
import org.pf.domain.User;
import org.pf.domain.UserAccount;
import org.pf.domain.enumeration.AccountType;
import org.pf.repository.TransactionRepository;
import org.pf.repository.UserAccountRepository;
import org.pf.repository.UserRepository;
import org.pf.repository.search.TransactionSearchRepository;
import org.pf.security.SecurityUtils;
import org.pf.service.TransactionService;
import org.pf.service.dto.TransactionDTO;
import org.pf.service.mapper.TransactionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Transaction.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService{

    public static final String YYYY_MM_DD_HH_MM_SS_S = "yyyy-MM-dd HH:mm:ss.S";
    public static final String END_OF_MONTH_STRING = "-12-31 23:59:59.0";
    private final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionSearchRepository transactionSearchRepository;

    private final UserRepository userRepository;

    private final UserAccountRepository userAccountRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper,
        TransactionSearchRepository transactionSearchRepository, UserRepository userRepository,
        UserAccountRepository userAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionSearchRepository = transactionSearchRepository;
        this.userRepository = userRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Save a transaction.
     *
     * @param transactionDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TransactionDTO save(TransactionDTO transactionDTO) {
        log.debug("Request to save Transaction : {}", transactionDTO);

        enforceSavingToCurrentUser(transactionDTO);

        Transaction transaction = transactionMapper.toEntity(transactionDTO);
        transaction = transactionRepository.save(transaction);
        TransactionDTO result = transactionMapper.toDto(transaction);
        transactionSearchRepository.save(transaction);
        return result;
    }

    private void enforceSavingToCurrentUser(TransactionDTO transactionDTO) {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if( ! login.isPresent()) {
            //This happens actually in test cases execution.
            return;
        }
        transactionDTO.setUserLogin(login.get());
        Optional<User> user = userRepository.findOneByLogin(login.get());
        transactionDTO.setUserId(user.get().getId());
    }

    /**
     * Get all the transactions.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> findAll() {
        log.debug("Request to get all Transactions");
        return transactionMapper.toDto(transactionRepository.findAll());
    }


    /**
     * Get all the transactions.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<TransactionDTO> findByUserLoginAndAccountId(String login, long userAccountId) {
        log.debug("Request to get all Transactions");

        List<TransactionDTO> transactions = transactionMapper.toDto(transactionRepository.findByUserLoginAndAccountId(login, userAccountId));

        UserAccount userAccount = userAccountRepository.findOne(userAccountId);
        computeBalance(userAccountId, userAccount.getType(), transactions, 0);

        return transactions;
    }


    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public TransactionDTO findOne(Long id) {
        log.debug("Request to get Transaction : {}", id);
        Transaction transaction = transactionRepository.findOne(id);
        return transactionMapper.toDto(transaction);
    }

    /**
     * Delete the transaction by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Transaction : {}", id);
        transactionRepository.delete(id);
        transactionSearchRepository.delete(id);
    }

    /**
     * Search for the transaction corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Transactions for query {}", query);
        Page<Transaction> result = transactionSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(transactionMapper::toDto);
    }


    private int getDepositSign(AccountType type) {
        switch (type) {
        case ASSET:
            return 1;
        case EXPENSE:
            return 1;

        case INCOME:
            return -1;
        case LIABILITY:
            return -1;
        default:
            return -1;
        }
    }

    private int getWithdrawSign(AccountType type) {
        switch (type) {
        case INCOME:
            return 1;
        case ASSET:
            return -1;
        case EXPENSE:
            return -1;
        case LIABILITY:
            return 1;
        default:
            return 1;
        }
    }

    private void computeBalance(long accountId, AccountType type, List<TransactionDTO> transactions,
        double initialBalance) {
        computeBalance(accountId, type, initialBalance, transactions);
    }

    public double computeBalance(long accountId, AccountType type, double initialBalance,
        List<TransactionDTO> listOfTrans) {
        double bal = initialBalance;
        for (TransactionDTO transactionDTO : listOfTrans) {
            //            listOfTans.get(i).getBalance()
            if (transactionDTO.getWithdrawAccountId().equals(accountId)) {
                bal += transactionDTO.getAmount() * getWithdrawSign(type);
            } else if (transactionDTO.getDepositAccountId().equals(accountId)) {
                bal += transactionDTO.getAmount() * getDepositSign(type);
            }
            transactionDTO.setBalance(transactionDTO.getBalance() + bal);
        }
        return bal;
    }

    public static String formatMoney(double number) {
        if (number == 0) { //double have some error factor
            return "0.00";
        }
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(number);
    }

    private List<String> generateYearList(ZonedDateTime minDate, ZonedDateTime maxDate) {
        if(minDate == null || maxDate == null) {
            return new ArrayList<>(); //just empty list
        } else {
            int minYear = minDate.getYear();
            int maxYear = maxDate.getYear();
            ArrayList<String> years = new ArrayList<>();
            //FIXME: How come year list includes one space string " "
            years.add(" "); //All years, means no filter
            for(int y = minYear; y <= maxYear; y++) {
                years.add(String.valueOf(y));
            }
            return years;
        }
    }

    public List<String> getYearList(String login) {

        ZonedDateTime minDate = transactionRepository.queryMinDate(login);
        ZonedDateTime maxDate = transactionRepository.queryMaxDate(login);

        if(minDate != null && maxDate != null) { //Null only if there is no transaction at all
            return generateYearList(minDate, maxDate);
        }
        return new ArrayList<>(); //empty list
    }

    private List<TransactionDTO> findYearTransactionsForIncomeAndExepnses(String login, Long userAccountId, Long year) {
        log.debug("Request to get all Transactions of account and in some year");

            ZonedDateTime fromDate = ZonedDateTime.parse(year + "-01-01 00:00:00.0", DateTimeFormatter.ofPattern(
                YYYY_MM_DD_HH_MM_SS_S).withZone(
            ZoneId.systemDefault()));
        ZonedDateTime toDate = ZonedDateTime.parse(year + END_OF_MONTH_STRING, DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(
            ZoneId.systemDefault()));

        List<TransactionDTO> transactions = transactionMapper.toDto(
            transactionRepository.findByLoginAndAccountIdAndYear(login, userAccountId, fromDate, toDate));

        UserAccount userAccount = userAccountRepository.findOne(userAccountId);
        computeBalance(userAccountId, userAccount.getType(), transactions, 0);
        return transactions;
    }

    private  List<TransactionDTO> findYearTransactionsForAssetAndLiability(String login, Long accountId, Long year) {

        //1) Get ppast years transactions to get past balance
        ZonedDateTime fromDate = ZonedDateTime.parse("1900-01-01 00:00:00.0", DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(
            ZoneId.systemDefault()));
        ZonedDateTime toDate = ZonedDateTime.parse((year-1) + END_OF_MONTH_STRING, DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(
            ZoneId.systemDefault()));
        List<TransactionDTO> transactions = transactionMapper.toDto(transactionRepository.findByLoginAndAccountIdAndYear(login, accountId, fromDate, toDate));

        UserAccount userAccount = userAccountRepository.findOne(accountId);
        computeBalance(accountId, userAccount.getType(), transactions, 0);
        //Might be there is no past transactions
        Double pastBalance = 0D;
        if(! transactions.isEmpty()) {
            pastBalance = transactions.get(transactions.size() - 1).getBalance();
        }

        //2) Get this year transactions

        ZonedDateTime thisYearStart = ZonedDateTime.parse(year + "-01-01 00:00:00.0", DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(
            ZoneId.systemDefault()));
        ZonedDateTime thisYearEnd   = ZonedDateTime.parse(year + END_OF_MONTH_STRING, DateTimeFormatter.ofPattern(
            YYYY_MM_DD_HH_MM_SS_S).withZone(
            ZoneId.systemDefault()));
        transactions = transactionMapper.toDto(transactionRepository.findByLoginAndAccountIdAndYear(login, accountId, thisYearStart, thisYearEnd));

        computeBalance(accountId, userAccount.getType(), transactions, pastBalance);
        return transactions;
    }

    public List<TransactionDTO> findYearTransactions(String login, Long userAccountId, Long year) {
        UserAccount userAccount = userAccountRepository.findOne(userAccountId);
        if(userAccount.getType() == AccountType.ASSET || userAccount.getType() == AccountType.LIABILITY) {
            return findYearTransactionsForAssetAndLiability(login, userAccountId, year);
        }
        return findYearTransactionsForIncomeAndExepnses(login, userAccountId, year);
    }

    public boolean isInvalidCurrencies(TransactionDTO transactionDTO) {
        long depositId = transactionDTO.getDepositAccountId();
        long withdrawId = transactionDTO.getWithdrawAccountId();
        UserAccount withdraw = userAccountRepository.findOne(withdrawId);
        UserAccount deposit = userAccountRepository.findOne(depositId);
        return (! withdraw.getCurrency().getName().contentEquals(deposit.getCurrency().getName()) );
    }


}
