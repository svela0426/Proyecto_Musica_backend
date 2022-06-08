package co.edu.uniandes.dse.musica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.musica.entities.CapituloEntity;

/**
 * Interface that persists a Capitulo.
 *
 * @author Esteban Gonzalez Ruales
 *
 */
@Repository
public interface CapituloRepository extends JpaRepository<CapituloEntity, Long> {

}