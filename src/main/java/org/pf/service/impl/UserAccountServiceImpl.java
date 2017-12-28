package org.pf.service.impl;

import org.pf.domain.User;
import org.pf.domain.UserAccount;
import org.pf.repository.UserAccountRepository;
import org.pf.repository.UserRepository;
import org.pf.repository.search.UserAccountSearchRepository;
import org.pf.security.SecurityUtils;
import org.pf.service.UserAccountService;
import org.pf.service.dto.UserAccountDTO;
import org.pf.service.mapper.UserAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing UserAccount.
 */
@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService{

    private final Logger log = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    private final UserAccountRepository userAccountRepository;

    private final UserAccountMapper userAccountMapper;

    private final UserAccountSearchRepository userAccountSearchRepository;

    private final UserRepository userRepository;

    public UserAccountServiceImpl(UserAccountRepository userAccountRepository, UserAccountMapper userAccountMapper,
        UserAccountSearchRepository userAccountSearchRepository,
        UserRepository userRepository) {
        this.userAccountRepository = userAccountRepository;
        this.userAccountMapper = userAccountMapper;
        this.userAccountSearchRepository = userAccountSearchRepository;
        this.userRepository = userRepository;
    }


    private void enforceSavingToCurrentUser(UserAccountDTO userAccountDTO) {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if( ! login.isPresent()) {
            //This happens actually in test cases execution.
            return;
        }
        userAccountDTO.setUserLogin(login.get());
        Optional<User> user = userRepository.findOneByLogin(login.get());
        userAccountDTO.setUserId(user.get().getId());
    }

    /**
     * Save a userAccount.
     *
     * @param userAccountDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public UserAccountDTO save(UserAccountDTO userAccountDTO) {
        log.debug("Request to save UserAccount : {}", userAccountDTO);
        enforceSavingToCurrentUser(userAccountDTO);
        UserAccount userAccount = userAccountMapper.toEntity(userAccountDTO);
        userAccount = userAccountRepository.save(userAccount);
        UserAccountDTO result = userAccountMapper.toDto(userAccount);
        userAccountSearchRepository.save(userAccount);
        return result;
    }

    /**
     * Check if an account text (title) already exist in database.
     * @param login logged in user
     * @param accountText text/title to be checked
     * @return already exist or not
     */
    public boolean isDuplicateName(String login, String accountText) {
        //Get User Credentials
        if(login == null) { //WEB ONLY- NOT TEST CASES.
            login = SecurityUtils.getCurrentUserLogin().get();
        }
        Optional<User> user = userRepository.findOneByLogin(login);
        long accountsCount = userAccountRepository.countByUser_IdAndText(user.get().getId(), accountText);
        return ( accountsCount > 0)? true: false;
    }

    /**
     * Get all the userAccounts.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserAccountDTO> findAll(Pageable pageable) {
        log.debug("Request to get all UserAccounts");
        return userAccountRepository.findAll(pageable)
            .map(userAccountMapper::toDto);
    }

    /**
     * Get one userAccount by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public UserAccountDTO findOne(Long id) {
        log.debug("Request to get UserAccount : {}", id);
        UserAccount userAccount = userAccountRepository.findOne(id);
        return userAccountMapper.toDto(userAccount);
    }

    /**
     * Delete the userAccount by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserAccount : {}", id);
        userAccountRepository.delete(id);
        userAccountSearchRepository.delete(id);
    }

    /**
     * Search for the userAccount corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserAccountDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of UserAccounts for query {}", query);
        Page<UserAccount> result = userAccountSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(userAccountMapper::toDto);
    }

    /**
     * Get all the userAccounts of current user.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)    public Page<UserAccountDTO> findByCurrentUser(String login, Pageable pageable) {
        log.debug("Request to get all UserAccounts of current logged in user");
        if(login == null) { //Web Interface ONLY (TEST cases will fail)
            login = SecurityUtils.getCurrentUserLogin().get();
        }
        return userAccountRepository.findByUser_LoginOrderByTypeAsc(login, pageable)
            .map(userAccountMapper::toDto);
    }

    @Override public List<UserAccountDTO> findByCurrentUser(String login) {
        log.debug("Request to get all UserAccounts of current logged in user");
        if(login == null) { //Web Interface ONLY (TEST cases will fail)
            login = SecurityUtils.getCurrentUserLogin().get();
        }
        return userAccountMapper.toDto(userAccountRepository.findByUser_LoginOrderByTypeAsc(login));
    }

}
