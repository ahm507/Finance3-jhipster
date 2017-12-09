package org.pf.repository;

import org.pf.domain.Currency;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the Currency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    @Query("select currency from Currency currency where currency.user.login = ?#{principal.username}")
    List<Currency> findByUserIsCurrentUser();

}
