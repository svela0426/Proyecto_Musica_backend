package co.edu.uniandes.dse.musica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.musica.entities.TemaEntity;

/**
 * Interface that persists a tema.
 *
 * @author Andres Parraga
 *
 */
@Repository
public interface TemaRepository extends JpaRepository<TemaEntity, Long> {

}