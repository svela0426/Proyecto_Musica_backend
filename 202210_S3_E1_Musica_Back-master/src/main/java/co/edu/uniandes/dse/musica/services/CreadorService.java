package co.edu.uniandes.dse.musica.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.helpers.EntityChecker;
import co.edu.uniandes.dse.musica.repositories.CreadorRepository;

/**
 * Class that manages the logic for the creador entity.
 * Uses CreadorRepository to manage persistence.
 *
 * @author Esteban Gonzalez Ruales
 */
@Service
@Import(EntityChecker.class)
public class CreadorService {

    @Autowired
    CreadorRepository creadorRepository;

    @Autowired
    EntityChecker entityChecker;

    private final String NOT_EMPTY_NAME = "Creador cannot have an empty name.";

    /**
     * Creates a Creador on the repository.
     *
     * @param creadorId Long: id of creador to look for.
     * @return CreadorEntity: the created creador.
     */
    @Transactional
    public CreadorEntity createCreador(CreadorEntity creadorEntity) throws IllegalOperationException {
        if (creadorEntity.getNombre().isEmpty())
            throw new IllegalOperationException(NOT_EMPTY_NAME);

        List<CreadorEntity> creadores = creadorRepository.findAll();
        for (CreadorEntity creador : creadores)
            if (creador.getNombre().equals(creadorEntity.getNombre()))
                throw new IllegalOperationException("Creador already exists.");

        return creadorRepository.save(creadorEntity);
    }

    /**
     * Gets certain Creador from the repository.
     *
     * @param creadorId Long: id of creador to look for.
     * @return CreadorEntity: the retrieved creador
     */
    @Transactional
    public CreadorEntity getCreador(Long creadorId) throws EntityNotFoundException {
        CreadorEntity creador = entityChecker.checkCreadorExists(creadorId);

        return creador;
    }

    /**
     * Updates certain Creador from the repository.
     *
     * @param creadorId     Long: id of creador to look for.
     * @param creadorEntity CreadorEntity: the entity with the updated
     *                      information
     * @return CreadorEntity: the creador that was updated.
     */
    @Transactional
    public CreadorEntity updateCreador(Long creadorId, CreadorEntity creadorEntity)
            throws EntityNotFoundException, IllegalOperationException {
        entityChecker.checkCreadorExists(creadorId);

        if (creadorEntity.getNombre().isEmpty())
            throw new IllegalOperationException(NOT_EMPTY_NAME);

        creadorEntity.setId(creadorId);
        return creadorRepository.save(creadorEntity);
    }

    /**
     * Deletes a Creador from the repository.
     *
     * @param creadorId Long: id of creador to look for.
     */
    @Transactional
    public void deleteCreador(Long creadorId) throws EntityNotFoundException {
        entityChecker.checkCreadorExists(creadorId);

        creadorRepository.deleteById(creadorId);
    }

    /**
     * Gets the list of Creadores.
     *
     * @return Collection of Creadores.
     */
    @Transactional
    public List<CreadorEntity> getCreadores() {
        return creadorRepository.findAll();
    }
}