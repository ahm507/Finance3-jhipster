package org.pf.repository;

import org.pf.domain.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UserAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    @Query("select user_account from UserAccount user_account where user_account.user.login = ?#{principal.username} order by type ASC")
    Page<UserAccount> findByUserIsCurrentUser(Pageable pageable);

    long countByUser_IdAndText(Long id, String text);
}
