package edu.wlu.graffiti.elasticsearch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.wlu.graffiti.bean.Inscription;

/**
 * Implementation of the InscriptionService interface
 * @author cooperbaird
 */
@Service
public class InscriptionServiceImpl implements InscriptionService {
	
	private InscriptionRepository inscriptionRepository;
	
	@Autowired
    public void setInscriptionRepository(InscriptionRepository inscriptionRepository) {
        this.inscriptionRepository = inscriptionRepository;
    }

	@Override
	public Inscription save(Inscription i) {
		return inscriptionRepository.save(i);
	}

	@Override
	public void delete(Inscription i) {
		inscriptionRepository.delete(i);
	}

	@Override
	public Iterable<Inscription> findAll() {
		return inscriptionRepository.findAll();
	}

	@Override
	public List<Inscription> findByContent(String content) {
		return inscriptionRepository.findByContent(content);
	}

}
