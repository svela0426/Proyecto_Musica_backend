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

import co.edu.uniandes.dse.musica.dto.CancionDTO;
import co.edu.uniandes.dse.musica.dto.CancionDetailDTO;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.AlbumCancionService;


@RestController
@RequestMapping("/albums")
public class AlbumCancionController {

	@Autowired
	private AlbumCancionService albumCancionService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping(value = "/{albumId}/canciones/{cancionId}")
	@ResponseStatus(code = HttpStatus.OK)
	public CancionDetailDTO addCancion(@PathVariable("cancionId") Long cancionId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException, IllegalOperationException {
		CancionEntity cancionEntity = albumCancionService.addCancion(albumId, cancionId);
		return modelMapper.map(cancionEntity, CancionDetailDTO.class);
	}

	@GetMapping(value = "/{albumId}/canciones/{cancionId}")
	@ResponseStatus(code = HttpStatus.OK)
	public CancionDetailDTO getCancion(@PathVariable("cancionId") Long cancionId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException, IllegalOperationException {
		CancionEntity cancionEntity = albumCancionService.getCancion(albumId, cancionId);
		return modelMapper.map(cancionEntity, CancionDetailDTO.class);
	}

	@PutMapping(value = "/{albumId}/canciones")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<CancionDetailDTO> addCanciones(@PathVariable("albumId") Long albumId, @RequestBody Set<CancionDTO> canciones)
			throws EntityNotFoundException, IllegalOperationException {
		Set<CancionEntity> entities = modelMapper.map(canciones, new TypeToken<Set<CancionEntity>>() {
		}.getType());
		Set<CancionEntity> cancionesList = albumCancionService.replaceCanciones(albumId, entities);
		return modelMapper.map(cancionesList, new TypeToken<Set<CancionDetailDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{albumId}/canciones")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<CancionDetailDTO> getCanciones(@PathVariable("albumId") Long albumId) throws EntityNotFoundException {
		Set<CancionEntity> cancionEntity = albumCancionService.getCanciones(albumId);
		return modelMapper.map(cancionEntity, new TypeToken<Set<CancionDetailDTO>>() {
		}.getType());
	}

	@DeleteMapping(value = "/{albumId}/canciones/{cancionId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeCancion(@PathVariable("cancionId") Long cancionId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException {
		albumCancionService.removeCancion(albumId, cancionId);
	}
}
