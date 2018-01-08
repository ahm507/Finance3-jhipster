package org.pf.service.impl;

import org.pf.domain.Currency;
import org.pf.domain.User;
import org.pf.repository.CurrencyRepository;
import org.pf.repository.UserRepository;
import org.pf.security.SecurityUtils;
import org.pf.service.CurrencyService;
import org.pf.service.dto.CurrencyDTO;
import org.pf.service.mapper.CurrencyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Currency.
 */
@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService{

    private final Logger log = LoggerFactory.getLogger(CurrencyServiceImpl.class);

    private final CurrencyRepository currencyRepository;

    private final CurrencyMapper currencyMapper;

    private final UserRepository userRepository;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository, CurrencyMapper currencyMapper,
        UserRepository userRepository) {
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
        this.userRepository = userRepository;
    }

    /**
     * Save a currency.
     *
     * @param currencyDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public CurrencyDTO save(CurrencyDTO currencyDTO) {
        log.debug("Request to save Currency : {}", currencyDTO);

        enforceSavingToCurrentUser(currencyDTO);
        Currency currency = currencyMapper.toEntity(currencyDTO);
        currency = currencyRepository.save(currency);
        return  currencyMapper.toDto(currency);
    }

    private void enforceSavingToCurrentUser(CurrencyDTO currencyDTO) {
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        if( ! login.isPresent()) {
            //This happens actually in test cases execution.
            return;
        }
        currencyDTO.setUserLogin(login.get());
        Optional<User> user = userRepository.findOneByLogin(login.get());
        currencyDTO.setUserId(user.get().getId());
    }


    /**
     * Get all the currencies.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<CurrencyDTO> findAll() {
        log.debug("Request to get all Currencies");
        return currencyRepository.findAll().stream()
            .map(currencyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<CurrencyDTO> findAllByCurrentUser(String login) {
        log.debug("Request to get all Currencies");
        return currencyRepository.findByUser_Login(login).stream()
            .map(currencyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one currency by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public CurrencyDTO findOne(Long id) {
        log.debug("Request to get Currency : {}", id);
        Currency currency = currencyRepository.findOne(id);
        return currencyMapper.toDto(currency);
    }

    /**
     * Delete the currency by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Currency : {}", id);
        currencyRepository.delete(id);
    }


    public boolean isDuplicateName(String login, String name) {
        if(login == null) { //Works with WEB ONLY - NOT TEST CASES. In Test cases, you must pass login parameter
            login = SecurityUtils.getCurrentUserLogin().get();
        }
        List<Currency> list = currencyRepository.findByNameAndUser_Login(name, login);
        return ! list.isEmpty();
    }

}
