package co.edu.uniandes.dse.musica.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.criteria.SetJoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.TemaEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;

import co.edu.uniandes.dse.musica.repositories.TemaRepository;
import co.edu.uniandes.dse.musica.repositories.PodcastRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PodcastTemaService {
	
	@Autowired
	private PodcastRepository podcastRepository;

	@Autowired
	private TemaRepository temaRepository;
	
	@Transactional
	public TemaEntity addTema(Long id, Long temaId) throws EntityNotFoundException {

		Optional<TemaEntity> temaEntity = temaRepository.findById(temaId);
		if (temaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);

		Optional<PodcastEntity> podcastEntity = podcastRepository.findById(id);
		if (podcastEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);

		podcastEntity.get().getTemas().add(temaEntity.get());

		return temaEntity.get();
	}
	
	
	/**
	 * Obtiene una colección de instancias de TemaEntity asociadas a una instancia
	 * de Podcast
	 *
	 * @param id Identificador de la instancia de un Podcast
	 * @return Colección de instancias de TemaEntity asociadas a la instancia de
	 *         Podcast
	 */
	@Transactional
	public Set<TemaEntity> getTemas(Long id) throws EntityNotFoundException {

		Optional<PodcastEntity> podcastEntity = podcastRepository.findById(id);
		if (podcastEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);

		return podcastEntity.get().getTemas();
	}

	/**
	 * Obtiene una instancia de TemaEntity asociada a una instancia de Podcast
	 *
	 * @param id   Identificador de la instancia de podcast
	 * @param temaId Identificador de la instancia de Tema
	 * @return La entidad del Autor asociada al libro
	 */
	@Transactional
	public TemaEntity getTema(Long id, Long temaId)
			throws EntityNotFoundException, IllegalOperationException {

		Optional<TemaEntity> temaEntity = temaRepository.findById(temaId);
		Optional<PodcastEntity> podcastEntity = podcastRepository.findById(id);


		if (temaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);

		if (podcastEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);

		if (podcastEntity.get().getTemas().contains(temaEntity.get()))
			return temaEntity.get();

		throw new IllegalOperationException("The tema is not associated to the podcast");
	}

	@Transactional
	/**
	 * Remplaza las instancias de Tema asociadas a una instancia de Podcast
	 *
	 * @param Id Identificador de la instancia de Podcast
	 * @param list    Colección de instancias de TemaEntity a asociar a instancia
	 *                de Podcast
	 * @return Nueva colección de TemaEntity asociada a la instancia de Podcast
	 */
	public Set<TemaEntity> replaceTemas(Long id, List<TemaEntity> list) throws EntityNotFoundException {

		Optional<PodcastEntity> podcastEntity = podcastRepository.findById(id);
		if (podcastEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);

		for (TemaEntity tema : list) {
			Optional<TemaEntity> temaEntity = temaRepository.findById(tema.getId());
			if (temaEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);

			if (!podcastEntity.get().getTemas().contains(temaEntity.get()))
				podcastEntity.get().getTemas().add(temaEntity.get());
		}
		return getTemas(id);
	}

	@Transactional
	/**
	 * Desasocia un Tema existente de un Podcast existente
	 *
	 * @param Id   Identificador de la instancia de Podcast
	 * @param temaId Identificador de la instancia de Tema
	 */
	public void removeTema(Long id, Long temaId) throws EntityNotFoundException {

		Optional<TemaEntity> temaEntity = temaRepository.findById(temaId);
		Optional<PodcastEntity> podcastEntity = podcastRepository.findById(id);


		if (temaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);
			
		if (podcastEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);
		podcastEntity.get().getTemas().remove(temaEntity.get());

	}

}