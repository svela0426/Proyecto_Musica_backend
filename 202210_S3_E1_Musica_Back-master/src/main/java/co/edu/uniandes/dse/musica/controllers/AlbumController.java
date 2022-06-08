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

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.dto.AlbumDTO;
import co.edu.uniandes.dse.musica.dto.AlbumDetailDTO;
import co.edu.uniandes.dse.musica.services.AlbumService;



@RestController
@RequestMapping("/albums")
// @RequestMapping("creadores/{id}/albumes")
public class AlbumController {

    @Autowired
    private AlbumService albumService; 

    @Autowired
    private ModelMapper modelMapper;
    

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    //AlbumDetailDTO albumDetailDTO
    public AlbumDTO create(@RequestBody AlbumDTO albumDTO) throws IllegalOperationException, EntityNotFoundException {
        AlbumEntity albumEntity = albumService.createAlbum(modelMapper.map(albumDTO, AlbumEntity.class)); 
        return modelMapper.map(albumEntity, AlbumDTO.class);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<AlbumDetailDTO> findAll() {
        List<AlbumEntity> albums = albumService.getAlbums(); 
        return modelMapper.map(albums, new TypeToken<List<AlbumDetailDTO>>(){}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AlbumDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        AlbumEntity albumEntity = albumService.getAlbum(id); 
        return modelMapper.map(albumEntity, AlbumDetailDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AlbumDTO update(@PathVariable("id") Long id, @RequestBody AlbumDTO albumDTO) throws EntityNotFoundException, IllegalOperationException {
        AlbumEntity albumEntity = albumService.updateAlbum(id, modelMapper.map(albumDTO, AlbumEntity.class));
        return modelMapper.map(albumEntity, AlbumDTO.class); 
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
        albumService.deleteAlbum(id); 
    }


}