package org.pf.repository;

import org.pf.domain.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the UserSettings entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

//    @Query("select user_settings from UserSettings user_settings where user_settings.user.login = ?#{principal.username}")
//    List<UserSettings> findAllByCurrentUser();

    List<UserSettings>  findByUser_login(String login);
}
