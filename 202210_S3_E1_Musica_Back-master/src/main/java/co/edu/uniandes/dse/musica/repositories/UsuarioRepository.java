package co.edu.uniandes.dse.musica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.musica.entities.UsuarioEntity;

/**
 * Interface that persists a Usuario.
 *
 * @author Svela0426
 *
 */


@Repository

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

}
