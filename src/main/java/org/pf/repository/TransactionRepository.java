package org.pf.repository;

import org.pf.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Transaction entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

//    @Query("select transaction from Transaction transaction where transaction.user.login = ?#{principal.username}")
//    List<Transaction> findByUserIsCurrentUser();

    @Query("select transaction from Transaction transaction where transaction.user.login = ?#{principal.username} order by date ASC")
    Page<Transaction> findByUserIsCurrentUser(Pageable pageable);

    @Query("select transaction from Transaction transaction "
        + "where transaction.user.login = ?#{principal.username} AND "
        + "(transaction.withdrawAccount.id = ?1 OR transaction.depositAccount.id = ?1) "
        + "order by date ASC")
    Page<Transaction> findByUserIsCurrentUserAndUserIdAccountId(Long userAccountId, Pageable pageable);

    Page<Transaction> findByUser_Login(String login, Pageable pageable);
}
