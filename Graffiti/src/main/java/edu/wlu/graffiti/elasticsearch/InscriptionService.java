package edu.wlu.graffiti.elasticsearch;

import java.util.List;

import edu.wlu.graffiti.bean.Inscription;

/**
 * Service interface for inscriptions
 * @author cooperbaird
 */
public interface InscriptionService {

	Inscription save(Inscription i);
	
	void delete(Inscription i);
		
	Iterable<Inscription> findAll();
	
	List<Inscription> findByContent(String content);
		
}
