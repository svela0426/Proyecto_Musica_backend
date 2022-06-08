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
import co.edu.uniandes.dse.musica.services.PlaylistCancionService;


@RestController
@RequestMapping("/playlists") 
public class PlaylistCancionController {
	
	@Autowired
	private PlaylistCancionService playlistCancionService;
	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping(value = "/{playlistId}/canciones/{cancionId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CancionDetailDTO addCancion(@PathVariable("cancionId") Long cancionId, @PathVariable("playlistId") Long playlistId)
                    throws EntityNotFoundException {
            CancionEntity cancionEntity = playlistCancionService.addCancion(playlistId, cancionId);
            return modelMapper.map(cancionEntity, CancionDetailDTO.class);
    }
	
	@GetMapping(value = "/{playlistId}/canciones/{cancionId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CancionDetailDTO getCancion(@PathVariable("cancionId") Long cancionId, @PathVariable("playlistId") Long playlistId)
                    throws EntityNotFoundException, IllegalOperationException {
            CancionEntity cancionEntity = playlistCancionService.getCancion(playlistId, cancionId);
            return modelMapper.map(cancionEntity, CancionDetailDTO.class);
    }
	
	@PutMapping(value = "/{playlistId}/canciones")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<CancionDetailDTO> addCanciones(@PathVariable("playlistId") Long playlistId, @RequestBody Set<CancionDTO> canciones)
			throws EntityNotFoundException {
		Set<CancionEntity> entities = modelMapper.map(canciones, new TypeToken<Set<CancionEntity>>() {
		}.getType());
		Set<CancionEntity> cancionesList = playlistCancionService.replaceCanciones(playlistId, entities);
		return modelMapper.map(cancionesList, new TypeToken<Set<CancionDetailDTO>>() {
		}.getType());
	}
	
	@GetMapping(value = "/{playlistId}/canciones")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<CancionDetailDTO> getCanciones(@PathVariable("playlistId") Long playlistId) throws EntityNotFoundException {
		Set<CancionEntity> cancionEntity = playlistCancionService.getCanciones(playlistId);
		return modelMapper.map(cancionEntity, new TypeToken<Set<CancionDetailDTO>>() {
		}.getType());
	}
	
	@DeleteMapping(value = "/{playlistId}/canciones/{cancionId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeCancion(@PathVariable("cancionId") Long cancionId, @PathVariable("playlistId") Long playlistId)
			throws EntityNotFoundException {
		playlistCancionService.removeCancion(playlistId, cancionId);
	}
	

}
