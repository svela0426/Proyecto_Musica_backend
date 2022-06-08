package co.edu.uniandes.dse.musica.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.entities.GeneroEntity;
import co.edu.uniandes.dse.musica.repositories.AlbumRepository;
import co.edu.uniandes.dse.musica.repositories.CancionRepository;
import co.edu.uniandes.dse.musica.repositories.CreadorRepository;
import co.edu.uniandes.dse.musica.repositories.GeneroRepository;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;

/**
 * Clase que implementa la conexion con la persistencia para la entidad de
 * Album.
 *
 * @author mar-cas3
 */

//@Slf4j
@Service
public class AlbumService {

    @Autowired
    AlbumRepository albumRepository;

	@Autowired
	CreadorRepository creadorRepository; 
	
	@Autowired
	CancionRepository cancionRespository; 

	@Autowired
	GeneroRepository generoRepository; 

    /**
	 * Se encarga de crear un Album en la base de datos.
	 *
	 * @param album Objeto de AlbumEntity con los datos nuevos
	 * @return Objeto de AlbumEntity con los datos nuevos y su ID.
     * @throws IllegalOperationException
	 */
	@Transactional
    public AlbumEntity createAlbum(AlbumEntity album) throws IllegalOperationException {
		if(album.getTitulo() == "") 
			throw new IllegalOperationException("Can't create album with emty title");
		return albumRepository.save(album);

    }

    /**
	 * Obtiene la lista de los registros de Album.
	 *
	 * @return Colección de objetos de AlbumEntity.
	 */
	@Transactional
	public List<AlbumEntity> getAlbums() {
		return albumRepository.findAll();
	}

	/**
	 * Obtiene los datos de una instancia de Album a partir de su ID.
	 *
	 * @param albumId Identificador de la instancia a consultar
	 * @return Instancia de AlbumEntity con los datos del Album consultado.
	 */
	@Transactional
	public AlbumEntity getAlbum(Long albumId) throws EntityNotFoundException {
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
		return albumEntity.get();
	}


	/**
	 * Actualizar una album por ID
	 *
	 * @param albumId    El ID del album a actualizar
	 * @param album La entidad del album con los cambios deseados
	 * @return La entidad del album luego de actualizarla
	 * @throws IllegalOperationException Si...
	 * @throws EntityNotFoundException Si el album no es encontrado
	 */
	@Transactional
	public AlbumEntity updateAlbum(Long albumId, AlbumEntity album) throws EntityNotFoundException, IllegalOperationException {

		// IOE if the Creador of the update is not valid
		
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

		if(album.getTitulo() == "") 
			throw new IllegalOperationException("Can't update album with emoty title");
		album.setId(albumId);
		return albumRepository.save(album);
		
	}

	/**
	 * Eliminar un album por ID
	 *
	 * @param albumId El ID del album a eliminar
	 * @throws IllegalOperationException si el album...
	 * @throws EntityNotFoundException si el album no existe
	 */

	@Transactional
	public void deleteAlbum(Long albumId) throws EntityNotFoundException, IllegalOperationException {

		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
		
		 Set <CreadorEntity> artistas = albumEntity.get().getArtistas(); 
		 if(!artistas.isEmpty())
		 	throw new IllegalOperationException("Can't delete album because it has associated artists");
		
		 Set <CancionEntity> canciones = albumEntity.get().getCanciones(); 
		 if(!canciones.isEmpty())
		 	throw new IllegalOperationException("Can't delete album because it has associated songs");

		Set<GeneroEntity> generos = albumEntity.get().getGeneros();
		if (!generos.isEmpty())
			throw new IllegalOperationException("Can't delete album because it has associated genres");
		
		albumRepository.deleteById(albumId);

	}
}

