package org.pf.repository.search;

import org.pf.domain.UserSettings;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the UserSettings entity.
 */
public interface UserSettingsSearchRepository extends ElasticsearchRepository<UserSettings, Long> {
}
