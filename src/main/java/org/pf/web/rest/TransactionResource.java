package org.pf.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.apache.commons.lang.NotImplementedException;
import org.pf.security.SecurityUtils;
import org.pf.service.TransactionService;
import org.pf.service.dto.TransactionDTO;
import org.pf.web.rest.errors.BadRequestAlertException;
import org.pf.web.rest.util.HeaderUtil;
import org.pf.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Transaction.
 */
@RestController
@RequestMapping("/api")
public class TransactionResource {

    private final Logger log = LoggerFactory.getLogger(TransactionResource.class);

    private static final String ENTITY_NAME = "transaction";

    private final TransactionService transactionService;

    public TransactionResource(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * POST  /transactions : Create a new transaction.
     *
     * @param transactionDTO the transactionDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new transactionDTO, or with status 400 (Bad Request) if the transaction has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/transactions")
    @Timed
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) throws URISyntaxException {
        log.debug("REST request to save Transaction : {}", transactionDTO);
        if (transactionDTO.getId() != null) {
            throw new BadRequestAlertException("A new transaction cannot already have an ID", ENTITY_NAME, "idexists");
        }

        if(transactionDTO.getAmount() < 0) {
            log.error("Transaction amount must be zero or more, ", ENTITY_NAME);
            throw new BadRequestAlertException("Amount is not allowed to be less than zero", ENTITY_NAME, "negative.value");
        }

        if(transactionService.isInvalidCurrencies(transactionDTO)) {
            log.error("Transactions must not be between the same user account, ", ENTITY_NAME);
            throw new BadRequestAlertException("Transactions should be between consistent currencies", ENTITY_NAME, "currency.value");
        }

        TransactionDTO result = transactionService.save(transactionDTO);
        return ResponseEntity.created(new URI("/api/transactions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /transactions : Updates an existing transaction.
     *
     * @param transactionDTO the transactionDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated transactionDTO,
     * or with status 400 (Bad Request) if the transactionDTO is not valid,
     * or with status 500 (Internal Server Error) if the transactionDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/transactions")
    @Timed
    public ResponseEntity<TransactionDTO> updateTransaction(@Valid @RequestBody TransactionDTO transactionDTO) throws URISyntaxException {
        log.debug("REST request to update Transaction : {}", transactionDTO);
        if (transactionDTO.getId() == null) {
            return createTransaction(transactionDTO);
        }
        TransactionDTO result = transactionService.save(transactionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, transactionDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /transactions : get all the transactions.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of transactions in body
     */
//    @GetMapping("/transactions")
//    @Timed
//    public ResponseEntity<List<TransactionDTO>> getAllTransactions(Pageable pageable) {
//        log.debug("REST request to get a page of Transactions");
//        Page<TransactionDTO> page = transactionService.findAllByCurrentUser(pageable);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/transactions");
//        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//    }


    /**
     * GET  /transactions : get all the transactions.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of transactions in body
     */
    @GetMapping("/transactions")
    @Timed
    public List<TransactionDTO> getAllTransactions(
        @RequestParam(required = false, value = "login") String login,
        @RequestParam(required = false, value = "userAccountId") Long userAccountId,
        @RequestParam(required = false, value = "year") Long year) {

        log.debug("REST request to get a page of Transactions");
        if(login == null) { //TEST cases must send login
            login = SecurityUtils.getCurrentUserLogin().get();
        }
        List<TransactionDTO> page;
        if(userAccountId == null && year == null) {
            throw new NotImplementedException(); //Not needed actually
        } else if (year == null){
            page = transactionService.findByUserLoginAndAccountId(login, userAccountId);
        } else if (userAccountId == null ) {
            throw new NotImplementedException(); //Not needed actually
        } else {
            //both are not null
            page = transactionService.findYearTransactions(login, userAccountId, year);
        }
        //log.debug("Getting transactions for userAccountID=" + userAccountId + ", count=" + page.getTotalElements());
        log.debug("Getting transactions for userAccountID={0}, count={1}" ,userAccountId, page.size());
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/transactions");
//        return new ResponseEntity<>(page, headers, HttpStatus.OK);
        return page;
    }

    /**
     * GET  /transactions/:id : get the "id" transaction.
     *
     * @param id the id of the transactionDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the transactionDTO, or with status 404 (Not Found)
     */
    @GetMapping("/transactions/{id}")
    @Timed
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) {
        log.debug("REST request to get Transaction : {}", id);
        TransactionDTO transactionDTO = transactionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(transactionDTO));
    }

    /**
     * DELETE  /transactions/:id : delete the "id" transaction.
     *
     * @param id the id of the transactionDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/transactions/{id}")
    @Timed
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.debug("REST request to delete Transaction : {}", id);
        transactionService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/transactions?query=:query : search for the transaction corresponding
     * to the query.
     *
     * @param query the query of the transaction search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/transactions")
    @Timed
    public ResponseEntity<List<TransactionDTO>> searchTransactions(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Transactions for query {}", query);
        Page<TransactionDTO> page = transactionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/transactions");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/transactions/yearList")
    @Timed
    public List<String> getYearList(@RequestParam(required = false, value = "login") String login) {
        log.debug("REST request to get year range that exists in transactions. Null if there are no transactions");
        if(login == null) { //TEST cases must send login
            login = SecurityUtils.getCurrentUserLogin().get();
        }
        return transactionService.getYearList(login);
    }


}
