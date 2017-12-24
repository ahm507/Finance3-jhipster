package org.pf.repository;

import org.pf.domain.Charts;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Charts entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChartsRepository extends JpaRepository<Charts, Long> {

}
