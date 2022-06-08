package co.edu.uniandes.dse.musica.helpers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import co.edu.uniandes.dse.musica.entities.CapituloEntity;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.repositories.CapituloRepository;
import co.edu.uniandes.dse.musica.repositories.CreadorRepository;
import co.edu.uniandes.dse.musica.repositories.PodcastRepository;

/**
 * Class to check if entities exists.
 *
 * @author Esteban Gonzalez Ruales
 */
public class EntityChecker {

	@Autowired
	private CreadorRepository creadorRepository;

	@Autowired
	private PodcastRepository podcastRepository;

	@Autowired
	private CapituloRepository capituloRepository;

	public final CreadorEntity checkCreadorExists(Long creatorId) throws EntityNotFoundException {
		Optional<CreadorEntity> creator = creadorRepository.findById(creatorId);
		return creator.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND));
	}

	public final PodcastEntity checkPodcastExists(Long PodcastId) throws EntityNotFoundException {
		Optional<PodcastEntity> podcast = podcastRepository.findById(PodcastId);
		return podcast.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PODCAST_NOT_FOUND));
	}

	public final CapituloEntity checkCapituloExists(Long chapterId) throws EntityNotFoundException {
		Optional<CapituloEntity> chapter = capituloRepository.findById(chapterId);
		return chapter.orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CAPITULO_NOT_FOUND));
	}
}