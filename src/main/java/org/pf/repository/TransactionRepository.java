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

    @Query("select transaction from Transaction transaction where transaction.user.login = ?#{principal.username}")

    Page<Transaction> findByUserIsCurrentUser(Pageable pageable);


}
