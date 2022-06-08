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

import co.edu.uniandes.dse.musica.dto.AlbumDTO;
import co.edu.uniandes.dse.musica.dto.AlbumDetailDTO;
import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.CreadorAlbumService;

/**
 * Clase que implementa el recurso "creadores/{id}/albums".
 *
 * @album ISIS2603
 */
@RestController
@RequestMapping("/creadores")
public class CreadorAlbumController {

	@Autowired
	private CreadorAlbumService creadorAlbumService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping(value = "/{creadorId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AlbumDetailDTO addAlbum(@PathVariable("albumId") Long albumId, @PathVariable("creadorId") Long creadorId)
			throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity albumEntity = creadorAlbumService.addAlbum(albumId, creadorId);
		return modelMapper.map(albumEntity, AlbumDetailDTO.class);
	}

	@GetMapping(value = "/{creadorId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AlbumDetailDTO getAlbum(@PathVariable("albumId") Long albumId, @PathVariable("creadorId") Long creadorId)
			throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity albumEntity = creadorAlbumService.getAlbum(albumId, creadorId);
		return modelMapper.map(albumEntity, AlbumDetailDTO.class);
	}

	@PutMapping(value = "/{creadorId}/albums")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<AlbumDetailDTO> replaceAlbums(@PathVariable("creadorId") Long creadorId, @RequestBody Set<AlbumDTO> albums)
			throws EntityNotFoundException {
		Set<AlbumEntity> entities = modelMapper.map(albums, new TypeToken<Set<AlbumEntity>>() {
		}.getType());
		Set<AlbumEntity> albumsList = creadorAlbumService.replaceAlbumes(creadorId, entities);
		return modelMapper.map(albumsList, new TypeToken<Set<AlbumDetailDTO>>() {
		}.getType());
	}


	@GetMapping(value = "/{creadorId}/albums")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<AlbumDetailDTO> getAlbums(@PathVariable("creadorId") Long creadorId) throws EntityNotFoundException {
		Set<AlbumEntity> albumEntity = creadorAlbumService.getAlbumes(creadorId);
		return modelMapper.map(albumEntity, new TypeToken<Set<AlbumDetailDTO>>() {
		}.getType());
	}


	@DeleteMapping(value = "/{creadorId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeAlbum(@PathVariable("albumId") Long albumId, @PathVariable("creadorId") Long creadorId)
			throws EntityNotFoundException {
		creadorAlbumService.removeAlbum(albumId, creadorId);
	}
}
