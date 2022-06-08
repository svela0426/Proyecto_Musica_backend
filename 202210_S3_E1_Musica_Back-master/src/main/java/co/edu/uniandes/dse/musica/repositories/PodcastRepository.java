package co.edu.uniandes.dse.musica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.musica.entities.PodcastEntity;

/**
 * Interface that persists a Podcast.
 *
 * @author Esteban Gonzalez Ruales
 *
 */
@Repository
public interface PodcastRepository extends JpaRepository<PodcastEntity, Long> {

}