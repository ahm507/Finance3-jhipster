package org.pf.service.impl;

import org.pf.domain.Transaction;
import org.pf.domain.User;
import org.pf.repository.TransactionRepository;
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

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionMapper transactionMapper,
        TransactionSearchRepository transactionSearchRepository,
        UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
        this.transactionSearchRepository = transactionSearchRepository;
        this.userRepository = userRepository;
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
    public Page<TransactionDTO> findAllByCurrentUser(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findByUserIsCurrentUser(pageable)
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
    public Page<TransactionDTO> findByUserIsCurrentUserAndUserIdAccountId(long userAccountId, Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findByUserIsCurrentUserAndUserIdAccountId(userAccountId, pageable)
            .map(transactionMapper::toDto);
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


}
