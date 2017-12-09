package org.pf.repository.search;

import org.pf.domain.Currency;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Currency entity.
 */
public interface CurrencySearchRepository extends ElasticsearchRepository<Currency, Long> {
}
