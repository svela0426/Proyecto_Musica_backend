package co.edu.uniandes.dse.musica.services;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TemaPodcastService {
	
	@Autowired
	private TemaRepository temaRepository;

	@Autowired
	private PodcastRepository podcastRepository;
	
	@Transactional
	public PodcastEntity addPodcast(Long Id, Long podcastId) throws EntityNotFoundException {

		Optional<PodcastEntity> podcastEntity = podcastRepository.findById(podcastId);
		if (podcastEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);

		Optional<TemaEntity> temaEntity = temaRepository.findById(Id);
		if (temaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);

		temaEntity.get().getPodcasts().add(podcastEntity.get());

		return podcastEntity.get();
	}
	
	
	/**
	 * Obtiene una colección de instancias de PodcastEntity asociadas a una instancia
	 * de Tema
	 *
	 * @param Id Identificador de la instancia de un Tema
	 * @return Colección de instancias de PodcastEntity asociadas a la instancia de
	 *         Tema
	 */
	@Transactional
	public List<PodcastEntity> getPodcasts(Long Id) throws EntityNotFoundException {

		Optional<TemaEntity> temaEntity = temaRepository.findById(Id);
		if (temaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);

		return temaEntity.get().getPodcasts();
	}

	/**
	 * Obtiene una instancia de PodcastEntity asociada a una instancia de Tema
	 *
	 * @param Id   Identificador de la instancia de tema
	 * @param podcastId Identificador de la instancia de Podcast
	 * @return La entidad del Autor asociada al libro
	 */
	@Transactional
	public PodcastEntity getPodcast(Long Id, Long podcastId)
			throws EntityNotFoundException, IllegalOperationException {

		Optional<PodcastEntity> podcastEntity = podcastRepository.findById(podcastId);
		Optional<TemaEntity> temaEntity = temaRepository.findById(Id);

		if (podcastEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);

		if (temaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);

		if (temaEntity.get().getPodcasts().contains(podcastEntity.get()))
			return podcastEntity.get();

		throw new IllegalOperationException("The podcast is not associated to the tema");
	}

	@Transactional
	/**
	 * Remplaza las instancias de Podcast asociadas a una instancia de Tema
	 *
	 * @param Id Identificador de la instancia de Tema
	 * @param list    Colección de instancias de PodcastEntity a asociar a instancia
	 *                de Tema
	 * @return Nueva colección de PodcastEntity asociada a la instancia de Tema
	 */
	public List<PodcastEntity> replacePodcasts(Long Id, List<PodcastEntity> list) throws EntityNotFoundException {

		Optional<TemaEntity> temaEntity = temaRepository.findById(Id);
		if (temaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);

		for (PodcastEntity podcast : list) {
			Optional<PodcastEntity> podcastEntity = podcastRepository.findById(podcast.getId());
			if (podcastEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);

			if (!temaEntity.get().getPodcasts().contains(podcastEntity.get()))
				temaEntity.get().getPodcasts().add(podcastEntity.get());
		}
		return getPodcasts(Id);
	}

	@Transactional
	/**
	 * Desasocia un Podcast existente de un Tema existente
	 *
	 * @param Id   Identificador de la instancia de Tema
	 * @param podcastId Identificador de la instancia de Podcast
	 */
	public void removePodcast(Long Id, Long podcastId) throws EntityNotFoundException {
		Optional<PodcastEntity> podcastEntity = podcastRepository.findById(podcastId);
		Optional<TemaEntity> temaEntity = temaRepository.findById(Id);

		if (podcastEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND);

		if (temaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.TEMA_NOT_FOUND);

		temaEntity.get().getPodcasts().remove(podcastEntity.get());

	}

}