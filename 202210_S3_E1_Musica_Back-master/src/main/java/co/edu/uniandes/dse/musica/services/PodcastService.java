package co.edu.uniandes.dse.musica.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.helpers.EntityChecker;
import co.edu.uniandes.dse.musica.repositories.PodcastRepository;

/**
 * Class that manages the logic for the Podcast entity.
 * Uses PodcastRepository to manage persistence.
 *
 * @author Esteban Gonzalez Ruales
 */
@Service
@Import(EntityChecker.class)
public class PodcastService {

    @Autowired
    PodcastRepository podcastRepository;

    @Autowired
    EntityChecker entityChecker;

    private final String NOT_EMPTY_TITLE = "Podcast cannot have an empty title.";

    /**
     * Creates a Podcast on the repository.
     *
     * @param poscastId Long: id of podcsat to look for.
     *
     * @return PodcastEntity: the created podcast.
     * @throws EntityNotFoundException
     */
    @Transactional
    public PodcastEntity createPodcast(PodcastEntity podcastEntity)
            throws IllegalOperationException, EntityNotFoundException {
        if (podcastEntity.getTitulo().isEmpty())
            throw new IllegalOperationException(NOT_EMPTY_TITLE);

        return podcastRepository.save(podcastEntity);
    }

    /**
     * Gets certain Podcast from the repository.
     *
     * @param podcastId Long: id of podcast to look for.
     * @return PodcastEntity: the retrieved podcast
     */
    @Transactional
    public PodcastEntity getPodcast(Long podcastId) throws EntityNotFoundException {
        PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

        return podcast;
    }

    /**
     * Updates certain Podcast from the repository.
     *
     * @param podcastId     Long: id of podcast to look for.
     * @param podcastEntity PodcastEntity: the entity with the updated
     *                      information
     * @return PodcastEntity: the podcast that was updated.
     * @throws IllegalOperationException
     */
    @Transactional
    public PodcastEntity updatePodcast(Long podcastId, PodcastEntity podcastEntity)
            throws EntityNotFoundException, IllegalOperationException {
        entityChecker.checkPodcastExists(podcastId);

        if (podcastEntity.getTitulo().isEmpty())
            throw new IllegalOperationException(NOT_EMPTY_TITLE);

        podcastEntity.setId(podcastId);
        return podcastRepository.save(podcastEntity);
    }

    /**
     * Deletes a Podcast from the repository.
     *
     * @param podcastId Long: id of podcast to look for.
     */
    @Transactional
    public void deletePodcast(Long podcastId) throws EntityNotFoundException {
        entityChecker.checkPodcastExists(podcastId);
        podcastRepository.deleteById(podcastId);
    }

    /**
     * Gets the list of Podcasts.
     *
     * @return Collection of Podcasts.
     */
    @Transactional
    public List<PodcastEntity> getPodcasts() {
        return podcastRepository.findAll();
    }
}