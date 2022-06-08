package co.edu.uniandes.dse.musica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.musica.entities.AlbumEntity;


/**
 * Interface that persists an album
 *
 * @author mar-cas3
 *
 */

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {
    
}
