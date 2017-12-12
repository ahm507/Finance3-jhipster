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
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Transaction.
 */
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService{

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
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAll(pageable)
            .map(transactionMapper::toDto);
    }

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findAllByCurrentUser(String login, Pageable pageable) {
        log.debug("Request to get all Transactions");
        if(login == null) { //WEB ONLY / NOT UNIT TEST
            login = SecurityUtils.getCurrentUserLogin().get();
        }
        return transactionRepository.findByUser_Login(login, pageable)
            .map(transactionMapper::toDto);
    }

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> findByUserLoginAndAccountId(String login, long userAccountId, Pageable pageable) {
        log.debug("Request to get all Transactions");

        Page<TransactionDTO> transactions = transactionRepository.findByUserLoginAndAccountId(login, userAccountId, pageable)
            .map(transactionMapper::toDto);

        UserAccount userAccount = userAccountRepository.findOne(userAccountId);
        computeBalance(userAccountId, userAccount.getType(), transactions);

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
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Transactions for query {}", query);
        Page<Transaction> result = transactionSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(transactionMapper::toDto);
    }


    int getDepositSign(AccountType type) {
        switch (type) {
        case INCOME:
            return -1;
        case ASSET:
            return 1;
        case EXPENSE:
            return 1;
        default:  // if(type.equals("liabilities")) {
            return -1;
        }
    }

    int getWithdrawSign(AccountType type) {
        switch (type) {
        case INCOME:
            return 1;
        case ASSET:
            return -1;
        case EXPENSE:
            return -1;
        default:  // if(type.equals("liabilities")) {
            return 1;
        }
    }

    public void computeBalance(long accountId, AccountType type, Page<TransactionDTO> transactions) {

        double bal = 0;
        List<TransactionDTO> listOfTrans = transactions.getContent();
        for(int i = 0 ; i < listOfTrans.size(); i++) {
//            listOfTans.get(i).getBalance()
            TransactionDTO transactionDTO = listOfTrans.get(i);

            if(transactionDTO.getWithdrawAccountId().equals(accountId)) {
                bal += transactionDTO.getAmount() * getWithdrawSign(type);
            }
            else if(transactionDTO.getDepositAccountId().equals(accountId)) {
                bal += transactionDTO.getAmount() * getDepositSign(type);
            }
            //			trans.balance += bal;
            transactionDTO.setBalance(transactionDTO.getBalance() + bal);

//            listOfTrans.set(i, transactionDTO);

        }

//        transactions.forEach(transactionDTO -> {
//
//            if(transactionDTO.getWithdrawAccountId().equals(accountId)) {
//                factor = getWithdrawSign(type);
//                bal += transactionDTO.getAmount() * factor;
//            }
//            else if(transactionDTO.getDepositAccountId().equals(accountId)) {
//                factor = getDepositSign(type);
//                bal += transactionDTO.getAmount() * factor;
//            }
//            //			trans.balance += bal;
//            transactionDTO.setBalance(transactionDTO.getBalance() + bal);
//        });
    }

    static public String formatMoney(double number) {
        //Format the numbers for display as example "100,050,676.574";
        if (number == 0) { //double have some error factor
            return "0.00";
        }
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(number);
    }

}
