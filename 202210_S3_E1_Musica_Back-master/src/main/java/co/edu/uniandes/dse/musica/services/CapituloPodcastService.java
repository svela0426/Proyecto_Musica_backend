package co.edu.uniandes.dse.musica.services;

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
 * Class to represent association of a capitulo with its podcast.
 *
 * @author Esteban Gonzalez Ruales
 */
@Service
@Import({ EntityChecker.class, CapituloService.class })
public class CapituloPodcastService {

    @Autowired
    EntityChecker entityChecker;

    @Autowired
    CapituloService capituloService;

    @Transactional
    public PodcastEntity getPodcastOfCapitulo(Long capituloId) throws EntityNotFoundException {
        CapituloEntity capitulo = entityChecker.checkCapituloExists(capituloId);

        return capitulo.getPodcast();
    }

    @Transactional
    public PodcastEntity setPodcastOfCapitulo(Long capituloId, Long podcastId)
            throws EntityNotFoundException, IllegalOperationException {
        CapituloEntity capitulo = entityChecker.checkCapituloExists(capituloId);
        PodcastEntity podcast = entityChecker.checkPodcastExists(podcastId);

        for (CapituloEntity cap : podcast.getCapitulos()) {
            if (cap.getTitulo().equals(capitulo.getTitulo()))
                throw new IllegalOperationException("Chapter already exists in podcast.");
        }

        capitulo.setPodcast(podcast);
        capituloService.updateCapitulo(capituloId, capitulo);
        return podcast;
    }
}