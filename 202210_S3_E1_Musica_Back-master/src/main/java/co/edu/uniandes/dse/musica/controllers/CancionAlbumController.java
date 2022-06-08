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
import co.edu.uniandes.dse.musica.services.CancionAlbumService;

/**
 * Clase que implementa el recurso "canciones/{id}/albums".
 *
 * @cancion ISIS2603
 */
@RestController
@RequestMapping("/canciones")
public class CancionAlbumController {

	@Autowired
	private CancionAlbumService cancionAlbumService;

	@Autowired
	private ModelMapper modelMapper;

	@GetMapping(value = "/{cancionId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AlbumDetailDTO getAlbum(@PathVariable("cancionId") Long cancionId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity albumEntity = cancionAlbumService.getAlbum(albumId, cancionId);
		return modelMapper.map(albumEntity, AlbumDetailDTO.class);
	}

	@GetMapping(value = "/{cancionId}/albums")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<AlbumDetailDTO> getAlbums(@PathVariable("cancionId") Long cancionId) throws EntityNotFoundException {
		Set<AlbumEntity> albumEntity = cancionAlbumService.getAlbumes(cancionId);
		return modelMapper.map(albumEntity, new TypeToken<Set<AlbumDetailDTO>>() {
		}.getType());
	}

	@PostMapping(value = "/{cancionId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.OK)
	public AlbumDetailDTO addAlbum(@PathVariable("albumId") Long albumId, @PathVariable("cancionId") Long cancionId)
			throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity albumEntity = cancionAlbumService.addAlbum(albumId, cancionId);
		return modelMapper.map(albumEntity, AlbumDetailDTO.class);
	}
	
	@PutMapping(value = "/{cancionId}/albums")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<AlbumDetailDTO> addAlbums(@PathVariable("cancionId") Long cancionId, @RequestBody Set<AlbumDTO> albums)
			throws EntityNotFoundException, IllegalOperationException {
		Set<AlbumEntity> entities = modelMapper.map(albums, new TypeToken<Set<AlbumEntity>>() {
		}.getType());
		Set<AlbumEntity> albumsList = cancionAlbumService.replaceAlbumes(cancionId, entities);
		return modelMapper.map(albumsList, new TypeToken<Set<AlbumDetailDTO>>() {
		}.getType());
	}

	@DeleteMapping(value = "/{cancionId}/albums/{albumId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeAlbum(@PathVariable("cancionId") Long cancionId, @PathVariable("albumId") Long albumId)
			throws EntityNotFoundException {
		cancionAlbumService.removeAlbum(albumId, cancionId);
	}
}
