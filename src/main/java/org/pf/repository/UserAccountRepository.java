package org.pf.repository;

import org.pf.domain.UserAccount;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the UserAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query("select user_account from UserAccount user_account where user_account.user.login = ?#{principal.username}")
    List<UserAccount> findByUserIsCurrentUser();

}
