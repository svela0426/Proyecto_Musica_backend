package co.edu.uniandes.dse.musica.controllers;

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

import co.edu.uniandes.dse.musica.dto.CreadorDTO;
import co.edu.uniandes.dse.musica.dto.PlaylistDTO;
import co.edu.uniandes.dse.musica.dto.PlaylistDetailDTO;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.entities.PlaylistEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.AlbumCreadorService;
import co.edu.uniandes.dse.musica.services.UsuarioPlaylistService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioPlaylistController {
	
	@Autowired
	private  UsuarioPlaylistService  usuarioPlaylistService;

	@Autowired
	private ModelMapper modelMapper;
	
	@PostMapping(value = "/{usuarioId}/playlists/{playlistId}")
    @ResponseStatus(code = HttpStatus.OK)
    public PlaylistDetailDTO addPlaylist(@PathVariable("playlistId") Long playlistId, @PathVariable("usuarioId") Long usuarioId)
                    throws EntityNotFoundException {
            PlaylistEntity playlistEntity = usuarioPlaylistService.addPlaylist(playlistId, usuarioId);
            return modelMapper.map(playlistEntity, PlaylistDetailDTO.class);
    }
	
	
	@GetMapping(value = "/{usuarioId}/playlists/{playlistId}")
    @ResponseStatus(code = HttpStatus.OK)
    public PlaylistDetailDTO getPlaylist(@PathVariable("playlistId") Long playlistId, @PathVariable("usuarioId") Long usuarioId)
                    throws EntityNotFoundException, IllegalOperationException {
            PlaylistEntity playlistEntity = usuarioPlaylistService.getPlaylist(playlistId, usuarioId);
            return modelMapper.map(playlistEntity, PlaylistDetailDTO.class);
    }
	
	@PutMapping(value = "/{usuarioId}/playlists")
    @ResponseStatus(code = HttpStatus.OK)
    public List<PlaylistDetailDTO> addPlaylists(@PathVariable("usuarioId") Long usuarioId, @RequestBody List<PlaylistDTO> playlists)
                    throws EntityNotFoundException {
            List<PlaylistEntity> entities = modelMapper.map(playlists, new TypeToken<List<PlaylistEntity>>() {
            }.getType());
            List<PlaylistEntity> playlistsList = usuarioPlaylistService.replacePlaylists(usuarioId, entities);
            return modelMapper.map(playlistsList, new TypeToken<List<PlaylistDetailDTO>>() {
            }.getType());        
    }
	
	
	@GetMapping(value = "/{usuarioId}/playlists")
    @ResponseStatus(code = HttpStatus.OK)
    public List<PlaylistDetailDTO> getPlaylists(@PathVariable("usuarioId") Long usuarioId) throws EntityNotFoundException {
            List<PlaylistEntity> playlistEntity = usuarioPlaylistService.getPlaylists(usuarioId);
            return modelMapper.map(playlistEntity, new TypeToken<List<PlaylistDetailDTO>>() {
            }.getType());
    }
	
	@DeleteMapping(value = "/{usuarioId}/playlists/{playlistId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removePlaylist(@PathVariable("playlistId") Long playlistId, @PathVariable("usuarioId") Long usuarioId)
                    throws EntityNotFoundException {
		usuarioPlaylistService.removePlaylist(playlistId, usuarioId);
    }
	
	

}