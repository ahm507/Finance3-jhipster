package org.pf.repository;

import org.pf.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Spring Data JPA repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT min(r.date) FROM Transaction r where r.user.login = ?1")
    ZonedDateTime queryMinDate(String login);

    @Query(value = "SELECT max(r.date) FROM Transaction r where r.user.login = ?1")
    ZonedDateTime queryMaxDate(String login);

    @Query("SELECT t FROM Transaction t "
        + "where t.user.login=?1 "
        + "AND (t.withdrawAccount.id =?2 OR t.depositAccount.id =?2) "
        + "AND (t.date BETWEEN ?3 AND ?4) "
        + "ORDER BY t.date")
    List<Transaction> findByLoginAndAccountIdAndYear(String login, Long userAccountId, ZonedDateTime fromDate, ZonedDateTime toDate);

    @Query("select transaction from Transaction transaction "
        + "where transaction.user.login = ?1 AND "
        + "(transaction.withdrawAccount.id = ?2 OR transaction.depositAccount.id = ?2) "
        + "order by date ASC")
    List<Transaction> findByUserLoginAndAccountId(String login, Long userAccountId);


    @Query("SELECT t FROM Transaction t "
        + "where t.user.login=?1 "
        + "AND (t.date BETWEEN ?2 AND ?3) "
        + "ORDER BY t.date")
    List<Transaction> findByUserLoginAndDateBetween(String login, ZonedDateTime fromDate, ZonedDateTime toDate);

    void deleteByUser_Login(String login);
}
