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
 * Class to represent association of a podcast with its creators.
 *
 * @author Esteban Gonzalez Ruales
 */
@Service
@Import({ EntityChecker.class, PodcastService.class })
public class PodcastCreadorService {

    @Autowired
    EntityChecker entityChecker;

    @Autowired
    PodcastService podcastService;

    @Transactional
    public CreadorEntity addCreadorToPodcast(Long podcastId, Long creadorId)
            throws EntityNotFoundException, IllegalOperationException {
        CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);
        PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

        podcast.getCreadores().add(creador);
        podcastService.updatePodcast(podcastId, podcast);
        return creador;
    }

    @Transactional
    public Set<CreadorEntity> getCreadoresOfPodcast(Long podcastId) throws EntityNotFoundException {
        PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

        return podcast.getCreadores();
    }

    @Transactional
    public CreadorEntity getCreadorOfPodcast(Long podcastId, Long creadorId)
            throws EntityNotFoundException, IllegalOperationException {
        CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);
        PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

        if (!podcast.getCreadores().contains(creador))
            throw new IllegalOperationException("The podcast does not contain the creador.");

        return creador;
    }

    @Transactional
    public Set<CreadorEntity> replaceCreadoresOfPodcast(Long podcastId, Set<CreadorEntity> creadores)
            throws EntityNotFoundException, IllegalOperationException {
        PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

        if (creadores.size() < 1)
            throw new IllegalOperationException("The list of creadores should have at least 1 creador.");

        podcast.setCreadores(new HashSet<CreadorEntity>());

        for (CreadorEntity creador : creadores) {
            CreadorEntity creadorEnt = entityChecker.checkCreadorExists(creador.getId());

            for (CreadorEntity cre : podcast.getCreadores()) {
                if (cre.getNombre().equals(creador.getNombre()))
                    throw new IllegalOperationException("Podcast cannot have two creadores with the same name");
            }

            addCreadorToPodcast(podcast.getId(), creadorEnt.getId());
        }

        podcastService.updatePodcast(podcastId, podcast);
        return getCreadoresOfPodcast(podcastId);
    }

    @Transactional
    public void removeCreadorOfPodcast(Long podcastId, Long creadorId)
            throws EntityNotFoundException, IllegalOperationException {
        CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);
        PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

        if (podcast.getCreadores().size() <= 1)
            throw new IllegalOperationException("Podcast has to have at least one creator");

        podcastService.updatePodcast(podcastId, podcast);
        podcast.getCreadores().remove(creador);
    }
}