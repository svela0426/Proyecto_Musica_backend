package co.edu.uniandes.dse.musica.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.GeneroEntity;
import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.repositories.GeneroRepository;
import co.edu.uniandes.dse.musica.repositories.AlbumRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AlbumGeneroService {
	
	@Autowired
	private AlbumRepository albumRepository;

	@Autowired
	private GeneroRepository generoRepository;
	
	@Transactional
	public GeneroEntity addGenero(Long albumId, Long generoId) throws EntityNotFoundException {
		log.info("Inicia proceso de agregarle un genero al album con id = {0}", albumId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if (generoEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

		albumEntity.get().getGeneros().add(generoEntity.get());
		log.info("Termina proceso de asociarle un un genero al album con id = {0}", albumId);
		return generoEntity.get();
	}
	
	
	/**
	 * Obtiene una colección de instancias de GeneroEntity asociadas a una instancia
	 * de Album
	 *
	 * @param albumId Identificador de la instancia de Album
	 * @return Colección de instancias de GeneroEntity asociadas a la instancia de
	 *         Album
	 */
	@Transactional
	public Set<GeneroEntity> getGeneros(Long albumId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos los generoes del album con id = {0}", albumId);
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
		log.info("Finaliza proceso de consultar todos los generoes del album con id = {0}", albumId);
		return albumEntity.get().getGeneros();
	}

	/**
	 * Obtiene una instancia de GeneroEntity asociada a una instancia de Album
	 *
	 * @param albumId   Identificador de la instancia de Album
	 * @param generoId Identificador de la instancia de Genero
	 * @return La entidad del Autor asociada al libro
	 */
	@Transactional
	public GeneroEntity getGenero(Long albumId, Long generoId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar un genero el album con id = {0}", albumId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);

		if (generoEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
		log.info("Termina proceso de consultar un genero el album con id = {0}", albumId);
		if (albumEntity.get().getGeneros().contains(generoEntity.get()))
			return generoEntity.get();

		throw new IllegalOperationException("The genero is not associated to the album");
	}

	@Transactional
	/**
	 * Remplaza las instancias de Genero asociadas a una instancia de Album
	 *
	 * @param albumId Identificador de la instancia de Album
	 * @param list    Colección de instancias de GeneroEntity a asociar a instancia
	 *                de Album
	 * @return Nueva colección de GeneroEntity asociada a la instancia de Album
	 */
	public Set<GeneroEntity> replaceGeneros(Long albumId, List<GeneroEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar los generoes del album con id = {0}", albumId);
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

		for (GeneroEntity genero : list) {
			Optional<GeneroEntity> generoEntity = generoRepository.findById(genero.getId());
			if (generoEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

			if (!albumEntity.get().getGeneros().contains(generoEntity.get()))
				albumEntity.get().getGeneros().add(generoEntity.get());
		}
		log.info("Termina proceso de reemplazar los generoes del album con id = {0}", albumId);
		return getGeneros(albumId);
	}

	@Transactional
	/**
	 * Desasocia un Genero existente de un Album existente
	 *
	 * @param albumId   Identificador de la instancia de Album
	 * @param generoId Identificador de la instancia de Genero
	 * @throws IllegalOperationException 
	 */
	public void removeGenero(Long albumId, Long generoId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de borrar un generoes del album con id = {0}", albumId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);

		if (generoEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
		
		try {
			albumEntity.get().getGeneros().remove(generoEntity.get());
			if (albumEntity.get().getGeneros().isEmpty()) {
				throw new IllegalOperationException("Un album debe tener por lo menos un genero");
			}
		} catch (Exception e) {
			throw e;
		}

		log.info("Termina proceso de borrar un generoes del album con id = {0}", albumId);
	}

}