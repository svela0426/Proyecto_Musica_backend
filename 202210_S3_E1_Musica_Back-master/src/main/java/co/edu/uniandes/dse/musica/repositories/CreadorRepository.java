package co.edu.uniandes.dse.musica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.musica.entities.CreadorEntity;

/**
 * Interface that persists a Creador.
 *
 * @author Esteban Gonzalez Ruales
 *
 */
@Repository
public interface CreadorRepository extends JpaRepository<CreadorEntity, Long> {

}