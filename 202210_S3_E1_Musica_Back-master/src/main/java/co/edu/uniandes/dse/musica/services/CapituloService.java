package co.edu.uniandes.dse.musica.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.CapituloEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.helpers.EntityChecker;
import co.edu.uniandes.dse.musica.repositories.CapituloRepository;

/**
 * Class that manages the logic for the Creator entity.
 * Uses CapituloRepository to manage persistence.
 *
 * @author Esteban Gonzalez Ruales
 */
@Service
@Import(EntityChecker.class)
public class CapituloService {

    @Autowired
    CapituloRepository capituloRepository;

    @Autowired
    EntityChecker entityChecker;

    final private String NOT_EMPTY_NAME = "Chapter cannot have an empty name.";

    /**
     * Creates a Capitulo on the repository.
     *
     * @param capituloId Long: id of capitulo to look for.
     * @return CapituloEntity: the created capitulo
     * @throws EntityNotFoundException
     */
    @Transactional
    public CapituloEntity createCapitulo(CapituloEntity capituloEntity)
            throws IllegalOperationException, EntityNotFoundException {
        if (capituloEntity.getTitulo().isEmpty())
            throw new IllegalOperationException(NOT_EMPTY_NAME);

        return capituloRepository.save(capituloEntity);
    }

    /**
     * Gets certain Capitulo from the repository.
     *
     * @param capituloId Long: id of capitulo to look for.
     * @return CapituloEntity: the retrieved capitulo
     */
    @Transactional
    public CapituloEntity getCapitulo(Long capituloId) throws EntityNotFoundException {
        CapituloEntity capitulo = entityChecker.checkCapituloExists(capituloId);

        return capitulo;
    }

    /**
     * Updates certain Capitulo from the repository.
     *
     * @param capituloId     Long: id of capitulo to look for.
     * @param capituloEntity CapituloEntity: the entity with the updated
     *                       information
     * @return CapituloEntity: the capitulo that was updated.
     */
    @Transactional
    public CapituloEntity updateCapitulo(Long capituloId, CapituloEntity capituloEntity)
            throws EntityNotFoundException, IllegalOperationException {
        if (capituloEntity.getTitulo().isEmpty())
            throw new IllegalOperationException(NOT_EMPTY_NAME);

        entityChecker.checkCapituloExists(capituloId);

        capituloEntity.setId(capituloId);
        return capituloRepository.save(capituloEntity);
    }

    /**
     * Deletes a Capitulo from the repository.
     *
     * @param capituloId Long: id of capitulo to look for.
     * @throws IllegalOperationException
     */
    @Transactional
    public void deleteCapitulo(Long capituloId) throws EntityNotFoundException, IllegalOperationException {
        entityChecker.checkCapituloExists(capituloId);

        capituloRepository.deleteById(capituloId);
    }

    /**
     * Gets the list of all Capitulos from the repository.
     *
     * @return List: Collection of capitulos.
     */
    @Transactional
    public List<CapituloEntity> getCapitulos() {
        return capituloRepository.findAll();
    }
}