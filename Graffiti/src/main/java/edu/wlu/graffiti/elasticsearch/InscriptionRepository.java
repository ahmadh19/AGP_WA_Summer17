package edu.wlu.graffiti.elasticsearch;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import edu.wlu.graffiti.bean.Inscription;

/**
 * Repository for the inscriptions
 * @author cooperbaird
 */
public interface InscriptionRepository extends ElasticsearchRepository<Inscription, String> {
	
	List<Inscription> findByContent(String content);
	
}
