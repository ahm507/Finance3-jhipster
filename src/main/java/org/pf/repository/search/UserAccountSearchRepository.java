package org.pf.repository.search;

import org.pf.domain.UserAccount;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the UserAccount entity.
 */
public interface UserAccountSearchRepository extends ElasticsearchRepository<UserAccount, Long> {
}
