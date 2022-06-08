package co.edu.uniandes.dse.musica.services;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.musica.entities.CapituloEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.helpers.EntityChecker;

/**
 * Class to represent association of podcasts with its capitulos.
 *
 * @author Esteban Gonzalez Ruales
 */
@Service
@Import({ EntityChecker.class, CapituloService.class, PodcastService.class })
public class PodcastCapituloService {

	@Autowired
	EntityChecker entityChecker;

	@Autowired
	PodcastService podcastService;

	@Autowired
	CapituloService capituloService;

	@Transactional
	public CapituloEntity addCapituloToPodcast(Long podcastId, Long capituloId)
			throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);
		CapituloEntity capitulo = entityChecker.checkCapituloExists(capituloId);

		for (CapituloEntity capituloEntity : podcast.getCapitulos()) {
			if (capituloEntity.getTitulo().equals(capitulo.getTitulo()))
				throw new IllegalOperationException("Chapter already exists in podcast.");
		}

		capitulo.setPodcast(podcast);
		podcast.getCapitulos().add(capitulo);

		podcastService.updatePodcast(podcastId, podcast);
		return capitulo;
	}

	@Transactional
	public Set<CapituloEntity> getCapitulosOfPodcast(Long podcastId) throws EntityNotFoundException {
		PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

		return podcast.getCapitulos();
	}

	@Transactional
	public CapituloEntity getCapituloOfPodcast(Long podcastId, Long capituloId)
			throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);
		CapituloEntity capitulo = entityChecker.checkCapituloExists(capituloId);

		if (!podcast.getCapitulos().contains(capitulo))
			throw new IllegalOperationException("The podcast does not contain the specified capitulo.");

		return capitulo;
	}

	@Transactional
	public Set<CapituloEntity> replaceCapitulosOfPodcast(Long podcastId, Set<CapituloEntity> capitulos)
			throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

		var capituloEntities = new HashSet<CapituloEntity>();
		for (CapituloEntity capitulo : capitulos)
			capituloEntities.add(capituloService.getCapitulo(capitulo.getId()));

		var nombres = new HashSet<String>();
		for (CapituloEntity capitulo : capituloEntities) {
			if (nombres.contains(capitulo.getTitulo())) {
				throw new IllegalOperationException("Capitulos list contains a duplicate capitulo titulo.");
			}
			nombres.add(capitulo.getTitulo());
		}

		for (CapituloEntity capitulo : podcast.getCapitulos())
			capitulo.setPodcast(null);

		podcast.setCapitulos(new HashSet<CapituloEntity>());

		for (CapituloEntity capitulo : capitulos) {
			CapituloEntity capituloEnt = entityChecker.checkCapituloExists(capitulo.getId());
			addCapituloToPodcast(podcast.getId(), capituloEnt.getId());
		}

		for (CapituloEntity capitulo : capituloService.getCapitulos()) {
			if (capitulo.getPodcast() == null)
				capituloService.deleteCapitulo(capitulo.getId());
		}

		podcastService.updatePodcast(podcastId, podcast);
		return getCapitulosOfPodcast(podcastId);
	}

	@Transactional
	public void removeCapituloOfPodcast(Long podcastId, Long capituloId)
			throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);
		CapituloEntity capitulo = entityChecker.checkCapituloExists(capituloId);

		podcast.getCapitulos().remove(capitulo);
		podcastService.updatePodcast(podcastId, podcast);
	}
}