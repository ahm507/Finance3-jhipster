package org.pf.repository.search;

import org.pf.domain.Charts;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Charts entity.
 */
public interface ChartsSearchRepository extends ElasticsearchRepository<Charts, Long> {
}
