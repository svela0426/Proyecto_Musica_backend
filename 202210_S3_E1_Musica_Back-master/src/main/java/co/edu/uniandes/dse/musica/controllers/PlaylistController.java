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

import co.edu.uniandes.dse.musica.dto.PlaylistDTO;
import co.edu.uniandes.dse.musica.dto.PlaylistDetailDTO;
import co.edu.uniandes.dse.musica.entities.PlaylistEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.PlaylistService;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

	 @Autowired
     private PlaylistService playlistService;

     @Autowired
     private ModelMapper modelMapper;
     
     @PostMapping
     @ResponseStatus(code = HttpStatus.CREATED)
      public PlaylistDTO create(@RequestBody PlaylistDTO playlistDTO) throws IllegalOperationException, EntityNotFoundException {
    	 	 PlaylistEntity playlistEntity = playlistService.createPlaylist(modelMapper.map(playlistDTO, PlaylistEntity.class));
             return modelMapper.map(playlistEntity, PlaylistDTO.class);
          }
     
     @GetMapping
     @ResponseStatus(code = HttpStatus.OK)
     public List<PlaylistDetailDTO> findAll() {
             List<PlaylistEntity> playlists = playlistService.getPlaylists();
             return modelMapper.map(playlists, new TypeToken<List<PlaylistDetailDTO>>() {
             }.getType());
     }
     
     @GetMapping(value = "/{id}")
     @ResponseStatus(code = HttpStatus.OK)
     public PlaylistDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
    	 	 PlaylistEntity playlistEntity = playlistService.getPlaylist(id);
             return modelMapper.map(playlistEntity, PlaylistDetailDTO.class);
     }
     
     @PutMapping(value = "/{id}")
     @ResponseStatus(code = HttpStatus.OK)
     public PlaylistDTO update(@PathVariable("id") Long id, @RequestBody PlaylistDTO playlistDTO)
                     throws EntityNotFoundException, IllegalOperationException {
    	 	 PlaylistEntity playlistEntity = playlistService.updatePlaylist(id, modelMapper.map(playlistDTO, PlaylistEntity.class));
             return modelMapper.map(playlistEntity, PlaylistDTO.class);
     }
     
     @DeleteMapping(value = "/{id}")
     @ResponseStatus(code = HttpStatus.NO_CONTENT)
     public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
    	 playlistService.deletePlaylist(id);
     }
     
}
