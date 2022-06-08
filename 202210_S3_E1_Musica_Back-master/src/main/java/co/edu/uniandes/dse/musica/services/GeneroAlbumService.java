package co.edu.uniandes.dse.musica.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.GeneroEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.repositories.AlbumRepository;
import co.edu.uniandes.dse.musica.repositories.GeneroRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeneroAlbumService {
	
	@Autowired
	private GeneroRepository generoRepository;

	@Autowired
	private AlbumRepository albumRepository;
	
	@Transactional
	public AlbumEntity addAlbum(Long generoId, Long albumId) throws EntityNotFoundException {
		log.info("Inicia proceso de agregarle un album al genero con id = {0}", generoId);
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if (generoEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

		generoEntity.get().getAlbumes().add(albumEntity.get());
		log.info("Termina proceso de asociarle un un album al genero con id = {0}", generoId);
		return albumEntity.get();
	}
	
	
	/**
	 * Obtiene una colección de instancias de AlbumEntity asociadas a una instancia
	 * de Genero
	 *
	 * @param generoId Identificador de la instancia de Genero
	 * @return Colección de instancias de AlbumEntity asociadas a la instancia de
	 *         Genero
	 */
	@Transactional
	public List<AlbumEntity> getAlbums(Long generoId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos los albumes del genero con id = {0}", generoId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if (generoEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
		log.info("Finaliza proceso de consultar todos los albumes del genero con id = {0}", generoId);
		return generoEntity.get().getAlbumes();
	}

	/**
	 * Obtiene una instancia de AlbumEntity asociada a una instancia de Genero
	 *
	 * @param generoId   Identificador de la instancia de Genero
	 * @param albumId Identificador de la instancia de Album
	 * @return La entidad del Autor asociada al libro
	 */
	@Transactional
	public AlbumEntity getAlbum(Long generoId, Long albumId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar un album el genero con id = {0}", generoId);
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);

		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

		if (generoEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
		log.info("Termina proceso de consultar un album el genero con id = {0}", generoId);
		if (generoEntity.get().getAlbumes().contains(albumEntity.get()))
			return albumEntity.get();

		throw new IllegalOperationException("The album is not associated to the genero");
	}

	@Transactional
	/**
	 * Remplaza las instancias de Album asociadas a una instancia de Genero
	 *
	 * @param generoId Identificador de la instancia de Genero
	 * @param list    Colección de instancias de AlbumEntity a asociar a instancia
	 *                de Genero
	 * @return Nueva colección de AlbumEntity asociada a la instancia de Genero
	 */
	public List<AlbumEntity> replaceAlbums(Long generoId, List<AlbumEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar los albumes del genero con id = {0}", generoId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if (generoEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

		for (AlbumEntity album : list) {
			Optional<AlbumEntity> albumEntity = albumRepository.findById(album.getId());
			if (albumEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

			if (!generoEntity.get().getAlbumes().contains(albumEntity.get()))
				generoEntity.get().getAlbumes().add(albumEntity.get());
		}
		log.info("Termina proceso de reemplazar los albumes del genero con id = {0}", generoId);
		return getAlbums(generoId);
	}

	@Transactional
	/**
	 * Desasocia un Album existente de un Genero existente
	 *
	 * @param generoId   Identificador de la instancia de Genero
	 * @param albumId Identificador de la instancia de Album
	 */
	public void removeAlbum(Long generoId, Long albumId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar un albumes del genero con id = {0}", generoId);
		Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);

		if (albumEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

		if (generoEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

		generoEntity.get().getAlbumes().remove(albumEntity.get());

		log.info("Termina proceso de borrar un albumes del genero con id = {0}", generoId);
	}

}
