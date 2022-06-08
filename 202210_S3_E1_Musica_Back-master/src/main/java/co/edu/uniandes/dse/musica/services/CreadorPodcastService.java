package co.edu.uniandes.dse.musica.services;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.helpers.EntityChecker;

/**
 * Class to represent association of a creator with its podcasts.
 *
 * @author Esteban Gonzalez Ruales
 */
@Service
@Import({ EntityChecker.class, CreadorService.class })
public class CreadorPodcastService {

	@Autowired
	EntityChecker entityChecker;

	@Autowired
	CreadorService creadorService;

	@Transactional
	public PodcastEntity addPodcastToCreador(Long creadorId, Long podcastId)
			throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);
		PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

		for (PodcastEntity podcastEntity : creador.getPodcasts())
			if (podcastEntity.getTitulo().equals(podcast.getTitulo()))
				throw new IllegalOperationException(
						"Podcast with that title has already been created in the creator.");

		creador.getPodcasts().add(podcast);
		creadorService.updateCreador(creadorId, creador);
		return podcast;
	}

	@Transactional
	public Set<PodcastEntity> getPodcastsOfCreador(Long creadorId) throws EntityNotFoundException {
		CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);

		return creador.getPodcasts();
	}

	@Transactional
	public PodcastEntity getPodcastOfCreador(Long creadorId, Long podcastId)
			throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);
		PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

		if (!creador.getPodcasts().contains(podcast))
			throw new IllegalOperationException("The creator does not contain the podcast.");

		return podcast;
	}

	@Transactional
	public Set<PodcastEntity> replacePodcastsOfCreador(Long creadorId, Set<PodcastEntity> podcasts)
			throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);

		creador.setPodcasts(new HashSet<PodcastEntity>());

		for (PodcastEntity podcast : podcasts) {
			PodcastEntity podcastEnt = entityChecker.checkPodcastExists(podcast.getId());

			addPodcastToCreador(creador.getId(), podcastEnt.getId());
		}

		creadorService.updateCreador(creadorId, creador);
		return getPodcastsOfCreador(creadorId);
	}

	@Transactional
	public void removePodcastOfCreador(Long creadorId, Long podcastId)
			throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);
		PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

		creador.getPodcasts().remove(podcast);
		creadorService.updateCreador(creadorId, creador);
	}
}