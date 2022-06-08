package co.edu.uniandes.dse.musica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.musica.entities.GeneroEntity;


/**
 * Interface that persists a genre
 *
 * @author juancamilobonet2
 *
 */

@Repository
public interface GeneroRepository extends JpaRepository<GeneroEntity, Long> {
    
}