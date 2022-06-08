package co.edu.uniandes.dse.musica.services;


import java.util.ArrayList;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.entities.TemaEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.repositories.PodcastRepository;
import co.edu.uniandes.dse.musica.repositories.TemaRepository;

/**
 * Class that manages the logic for the Tema entity.
 * Uses PodcastRepository to manage persistence.
 *
 * @author Andres Parraga.
 */
@Service
public class TemaService {

	@Autowired
	TemaRepository temaRepository;
	
	@Autowired
	PodcastRepository podcastRepository;

	@Transactional
	public TemaEntity createtema (TemaEntity tema) throws IllegalOperationException {

		//Crea un nuevo tema y lo pone en la base de datos
		
		// podcast ya deben estar registradas 
		ArrayList<PodcastEntity> podcastsEntidad = new ArrayList<>();
		
		for(PodcastEntity podcast: tema.getPodcasts()) {
			Optional <PodcastEntity> podcastEntity = podcastRepository.findById(podcast.getId());
			if(podcastEntity.isEmpty()) 
				throw new IllegalOperationException("Some podcast is not valid");
				podcastsEntidad.add(podcastEntity.get());
		}
		tema.setPodcasts(podcastsEntidad);

		return temaRepository.save(tema);
	}
	
	@Transactional
	public List<TemaEntity> getTemas() {
		//retorna todos los temas en la base de datos
		return temaRepository.findAll();
	}
	
	@Transactional
	public TemaEntity getTema(Long id) throws EntityNotFoundException {
		//retorna el tema buscado, tira excepcion si no se encuentra
		
		Optional<TemaEntity> temaEntity = temaRepository.findById(id);
		if(temaEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);
		}
		return temaEntity.get();
	}
	
	@Transactional
	public TemaEntity updateTema(Long id, TemaEntity temaCambiado) throws EntityNotFoundException {
		//cambia un tema en la base de datos por otro, tira excepcion si no se encuentra el id
		Optional<TemaEntity> temaEntity = temaRepository.findById(id);
		if(temaEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);
		}
		
		temaCambiado.setId(id);
		
		return temaRepository.save(temaCambiado);
	}
	
	@Transactional
	public void deleteTema(Long id) throws EntityNotFoundException {
		//borra un tema de la BD, tira excepcion si no lo encuentra
		Optional<TemaEntity> temaEntity = temaRepository.findById(id);
		if(temaEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);
		}
		
	temaRepository.deleteById(id);
	}
	
	
	
}
