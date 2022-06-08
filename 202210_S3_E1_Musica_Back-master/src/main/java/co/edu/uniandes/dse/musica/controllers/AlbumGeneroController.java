package co.edu.uniandes.dse.musica.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import co.edu.uniandes.dse.musica.dto.GeneroDTO;
import co.edu.uniandes.dse.musica.dto.GeneroDetailDTO;
import co.edu.uniandes.dse.musica.entities.GeneroEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.AlbumGeneroService;



/**
 * Clase que implementa el recurso "albums/{id}/generos".
 *
 * @album ISIS2603
 */
@RestController
@RequestMapping("/albums")
public class AlbumGeneroController {

	@Autowired
	private AlbumGeneroService albumGeneroService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Busca y devuelve el genero con el ID recibido en la URL, relativo a un album.
	 *
	 * @param albumId El ID del autor del cual se busca el libro
	 * @param generoId   El ID del libro que se busca
	 * @return {@link GeneroDetailDTO} - El libro encontrado en el autor.
	 */
	@GetMapping(value = "/{albumId}/generos/{generoId}")
	@ResponseStatus(code = HttpStatus.OK)
	public GeneroDetailDTO getGenero(@PathVariable("albumId") Long albumId, @PathVariable("generoId") Long generoId)
			throws EntityNotFoundException, IllegalOperationException {
		GeneroEntity generoEntity = albumGeneroService.getGenero(albumId, generoId);
		return modelMapper.map(generoEntity, GeneroDetailDTO.class);
	}

	/**
	 * Busca y devuelve todos los generoes de un album.
	 *
	 * @param albumsId El ID del autor del cual se buscan los libros
	 * @return JSONArray {@link GeneroDetailDTO} - Los libros encontrados en el autor.
	 *         Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{albumId}/generos")
	@ResponseStatus(code = HttpStatus.OK)
	public List<GeneroDetailDTO> getGeneros(@PathVariable("albumId") Long albumId) throws EntityNotFoundException {
		List<GeneroEntity> generoEntity = new ArrayList<GeneroEntity>(albumGeneroService.getGeneros(albumId));
		return modelMapper.map(generoEntity, new TypeToken<List<GeneroDetailDTO>>() {
		}.getType());
	}

	/**
	 * Asocia un genero con un album
	 *
	 * @param albumId El ID del autor al cual se le va a asociar el libro
	 * @param generoId   El ID del libro que se asocia
	 * @return JSON {@link GeneroDetailDTO} - El libro asociado.
	 */
	@PostMapping(value = "/{albumId}/generos/{generoId}")
	@ResponseStatus(code = HttpStatus.OK)
	public GeneroDetailDTO addGenero(@PathVariable("albumId") Long albumId, @PathVariable("generoId") Long generoId)
			throws EntityNotFoundException {
		GeneroEntity generoEntity = albumGeneroService.addGenero(albumId, generoId);
		return modelMapper.map(generoEntity, GeneroDetailDTO.class);
	}

	/**
	 * Actualiza la lista de generoes de un album con la lista que se recibe en el
	 * cuerpo
	 *
	 * @param albumId El ID del autor al cual se le va a asociar el libro
	 * @param generos    JSONArray {@link GeneroDTO} - La lista de libros que se desea
	 *                 guardar.
	 * @return JSONArray {@link GeneroDetailDTO} - La lista actualizada.
	 */
	@PutMapping(value = "/{albumId}/generos")
	@ResponseStatus(code = HttpStatus.OK)
	public List<GeneroDetailDTO> replaceGeneros(@PathVariable("albumId") Long albumId, @RequestBody List<GeneroDTO> generos)
			throws EntityNotFoundException {
		List<GeneroEntity> entities = modelMapper.map(generos, new TypeToken<List<GeneroEntity>>() {
		}.getType());
		
		List<GeneroEntity> generosList = new ArrayList<GeneroEntity>();
		for (GeneroEntity entity : entities) {
			generosList.add(albumGeneroService.addGenero(albumId, entity.getId()));
		}
		return modelMapper.map(generosList, new TypeToken<List<GeneroDetailDTO>>() {
		}.getType());

	}

	/**
	 * Elimina la conexión entre el album y el genero recibidos en la URL.
	 *
	 * @param albumId El ID del album al cual se le va a desasociar el genero
	 * @param generoId   El ID del genero que se desasocia
	 * @throws IllegalOperationException 
	 */
	@DeleteMapping(value = "/{albumId}/generos/{generoId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeGenero(@PathVariable("albumId") Long albumId, @PathVariable("generoId") Long generoId)
			throws EntityNotFoundException, IllegalOperationException {
		albumGeneroService.removeGenero(albumId, generoId);
	}
}
