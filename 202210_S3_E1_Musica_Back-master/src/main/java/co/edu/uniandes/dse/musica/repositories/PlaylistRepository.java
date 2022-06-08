package co.edu.uniandes.dse.musica.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.musica.entities.PlaylistEntity;

/**
 * Interface that persists a Playlist.
 *
 * @author Jonathan Rivera
 *
 */

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long>{

}
