package co.edu.uniandes.dse.musica.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.repositories.AlbumRepository;
import co.edu.uniandes.dse.musica.repositories.CancionRepository;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;


/**
 * Clase que implementa la conexion con la persistencia para la entidad de
 * Cancion.
 *
 * @author mar-cas3
 */

//@Slf4j
@Service
public class CancionService {

    @Autowired
    CancionRepository cancionRepository; 
	
	@Autowired
    AlbumRepository albumRepository;

    /**
	 * Se encarga de crear un Cancion en la base de datos.
	 *
	 * @param cancion Objeto de CancionEntity con los datos nuevos
	 * @return Objeto de CancionEnity con los datos nuevos y su ID.
     * @throws IllegalOperationException
	 */
	@Transactional
    public CancionEntity createCancion(CancionEntity cancion) throws IllegalOperationException {

		return cancionRepository.save(cancion);

    }

    /**
	 * Obtiene la lista de los registros de Cancion.
	 *
	 * @return Colección de objetos de CancionEntity.
	 */
	@Transactional
	public List<CancionEntity> getCanciones() {
		return cancionRepository.findAll();
	}

	/**
	 * Obtiene los datos de una instancia de Cancion a partir de su ID.
	 *
	 * @param cancionId Identificador de la instancia a consultar
	 * @return Instancia de CancionEntity con los datos del Cancion consultado.
	 */
	@Transactional
	public CancionEntity getCancion(Long cancionId) throws EntityNotFoundException {
		Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId);
		if (cancionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
		return cancionEntity.get();
	}

	/**
	 * Actualizar un libro por ID
	 *
	 * @param cancionId    El ID de la cancionn a actualizar
	 * @param cancion La entidad de la cancion con los cambios deseados
	 * @return La entidad de la cancion luego de actualizarla
	 * @throws IllegalOperationException Si...
	 * @throws EntityNotFoundException Si la cancion no es encontrada
	 */

	@Transactional
	public CancionEntity updateCancion(Long cancionId, CancionEntity cancion) throws EntityNotFoundException, IllegalOperationException {
		Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId);
		if (cancionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);

		cancion.setId(cancionId);
		return cancionRepository.save(cancion);
	}

	/**
	 * Eliminar una cancion por ID
	 *
	 * @param cancionId El ID del album a eliminar
	 * @throws IllegalOperationException si la cancion...
	 * @throws EntityNotFoundException si la cancion no existe
	 */
	@Transactional
	public void deleteCancion(Long cancionId) throws EntityNotFoundException {
		Optional <CancionEntity> cancionEntity = cancionRepository.findById(cancionId);
		if (cancionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
		cancionRepository.deleteById(cancionId);
	}

	
    
}
