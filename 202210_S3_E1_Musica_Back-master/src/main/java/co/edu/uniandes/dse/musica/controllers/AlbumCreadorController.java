package co.edu.uniandes.dse.musica.controllers;

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

import co.edu.uniandes.dse.musica.dto.CreadorDTO;
import co.edu.uniandes.dse.musica.dto.CreadorDetailDTO;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.AlbumCreadorService;

/**
 * Clase que implementa el recurso "albumes/{id}/creadores".
 *
 * @album ISIS2603
 */
@RestController
@RequestMapping("/albums")
public class AlbumCreadorController {

	@Autowired
	private AlbumCreadorService albumCreadorService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping(value = "/{albumId}/creadores/{creadorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public CreadorDetailDTO addCreador(@PathVariable("albumId") Long albumId, @PathVariable("creadorId") Long creadorId)
			throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creadorEntity = albumCreadorService.addCreador(albumId, creadorId);
		return modelMapper.map(creadorEntity, CreadorDetailDTO.class);
	}

	@GetMapping(value = "/{albumId}/creadores/{creadorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public CreadorDetailDTO getCreador(@PathVariable("creadorId") Long creadorId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creadorEntity = albumCreadorService.getCreador(albumId, creadorId);
		return modelMapper.map(creadorEntity, CreadorDetailDTO.class);
	}

	@GetMapping(value = "/{albumId}/creadores")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<CreadorDetailDTO> getCreadores(@PathVariable("albumId") Long albumId) throws EntityNotFoundException {
		Set<CreadorEntity> creadorEntity = albumCreadorService.getArtistas(albumId);
		return modelMapper.map(creadorEntity, new TypeToken<Set<CreadorDetailDTO>>() {
		}.getType());
	}

	
	@PutMapping(value = "/{albumId}/creadores")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<CreadorDetailDTO> replaceArtistas(@PathVariable("albumId") Long albumId, @RequestBody Set<CreadorDTO> creadores)
			throws EntityNotFoundException, IllegalOperationException {
		Set<CreadorEntity> entities = modelMapper.map(creadores, new TypeToken<Set<CreadorEntity>>() {
		}.getType());
		Set<CreadorEntity> creadoresList = albumCreadorService.replaceArtistas(albumId, entities);
		return modelMapper.map(creadoresList, new TypeToken<Set<CreadorDetailDTO>>() {
		}.getType());

	}

	/**
	 * Elimina la conexión entre el libro y e autor recibidos en la URL.
	 *
	 * @param albumId El ID del autor al cual se le va a desasociar el libro
	 * @param creadorId   El ID del libro que se desasocia
	 */
	@DeleteMapping(value = "/{albumId}/creadores/{creadorId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeCreador(@PathVariable("albumId") Long albumId, @PathVariable("creadorId") Long creadorId)
			throws EntityNotFoundException {
		albumCreadorService.removeCreador(albumId, creadorId);
	}
}
