package org.pf.repository;

import org.pf.domain.UserSettings;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the UserSettings entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {

}
