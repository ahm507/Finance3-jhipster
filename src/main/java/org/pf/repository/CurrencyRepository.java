package org.pf.repository;

import org.pf.domain.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Currency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {

//    @Query("select currency from Currency currency where currency.user.login = ?#{principal.username}")
//    List<Currency> findByUserIsCurrentUser();

    List<Currency> findByUser_Login(String login);

    List<Currency> findByNameAndUser_Login(String userLogin, String name);
}
