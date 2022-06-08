package co.edu.uniandes.dse.musica.controllers;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniandes.dse.musica.dto.AlbumDTO;
import co.edu.uniandes.dse.musica.dto.AlbumDetailDTO;
import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.GeneroAlbumService;



/**
 * Clase que implementa el recurso "generos/{id}/albums".
 *
 * @genero ISIS2603
 */
@RestController
@RequestMapping("/generos")
public class GeneroAlbumController {

	@Autowired
	private GeneroAlbumService generoAlbumService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Busca y devuelve el libro con el ID recibido en la URL, relativo a un autor.
	 *
	 * @param generoId El ID del autor del cual se busca el libro
	 * @param albumId   El ID del libro que se busca
	 * @return {@link AlbumDetailDTO} - El libro encontrado en el autor.
	 */
	@GetMapping(value = "/{generoId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AlbumDetailDTO getAlbum(@PathVariable("generoId") Long generoId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity albumEntity = generoAlbumService.getAlbum(generoId, albumId);
		return modelMapper.map(albumEntity, AlbumDetailDTO.class);
	}

	/**
	 * Busca y devuelve todos los albumes de un genero.
	 *
	 * @param generosId El ID del autor del cual se buscan los libros
	 * @return JSONArray {@link AlbumDetailDTO} - Los libros encontrados en el autor.
	 *         Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{generoId}/albums")
	@ResponseStatus(code = HttpStatus.OK)
	public List<AlbumDetailDTO> getAlbums(@PathVariable("generoId") Long generoId) throws EntityNotFoundException {
		List<AlbumEntity> albumEntity = generoAlbumService.getAlbums(generoId);
		return modelMapper.map(albumEntity, new TypeToken<List<AlbumDetailDTO>>() {
		}.getType());
	}

	/**
	 * Asocia un album existente con un genero existente
	 *
	 * @param generoId El ID del autor al cual se le va a asociar el libro
	 * @param albumId   El ID del libro que se asocia
	 * @return JSON {@link AlbumDetailDTO} - El libro asociado.
	 */
	@PostMapping(value = "/{generoId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AlbumDetailDTO addAlbum(@PathVariable("generoId") Long generoId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException {
		AlbumEntity albumEntity = generoAlbumService.addAlbum(generoId, albumId);
		return modelMapper.map(albumEntity, AlbumDetailDTO.class);
	}

	/**
	 * Actualiza la lista de albumes de un genero con la lista que se recibe en el
	 * cuerpo
	 *
	 * @param generoId El ID del autor al cual se le va a asociar el libro
	 * @param albums    JSONArray {@link AlbumDTO} - La lista de libros que se desea
	 *                 guardar.
	 * @return JSONArray {@link AlbumDetailDTO} - La lista actualizada.
	 */
	@PutMapping(value = "/{generoId}/albums")
	@ResponseStatus(code = HttpStatus.OK)
	public List<AlbumDetailDTO> replaceAlbums(@PathVariable("generoId") Long generoId, @RequestBody List<AlbumDTO> albums)
			throws EntityNotFoundException {
		List<AlbumEntity> entities = modelMapper.map(albums, new TypeToken<List<AlbumEntity>>() {
		}.getType());
		
		List<AlbumEntity> albumsList = new ArrayList<AlbumEntity>();
		for (AlbumEntity entity : entities) {
			albumsList.add(generoAlbumService.addAlbum(generoId, entity.getId()));
		}
		return modelMapper.map(albumsList, new TypeToken<List<AlbumDetailDTO>>() {
		}.getType());

	}

	/**
	 * Elimina la conexión entre el genero y el album recibidos en la URL.
	 *
	 * @param generoId El ID del autor al cual se le va a desasociar el libro
	 * @param albumId   El ID del libro que se desasocia
	 */
	@DeleteMapping(value = "/{generoId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeAlbum(@PathVariable("generoId") Long generoId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException {
		generoAlbumService.removeAlbum(generoId, albumId);
	}
}