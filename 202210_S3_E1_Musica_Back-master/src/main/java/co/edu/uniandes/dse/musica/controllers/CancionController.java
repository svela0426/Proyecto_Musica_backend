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

import co.edu.uniandes.dse.musica.dto.CancionDTO;
import co.edu.uniandes.dse.musica.dto.CancionDetailDTO;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.CancionService;

@RestController
@RequestMapping("/canciones")
public class CancionController {

    @Autowired
    private CancionService cancionService; 

    @Autowired
    private ModelMapper modelMapper;
    

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CancionDTO create(@RequestBody CancionDTO cancionDTO) throws IllegalOperationException {
        CancionEntity cancionEntity = cancionService.createCancion(modelMapper.map(cancionDTO, CancionEntity.class));
        return modelMapper.map(cancionEntity, CancionDTO.class);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CancionDTO> findAll() {
        List<CancionEntity> canciones = cancionService.getCanciones(); 
        return modelMapper.map(canciones, new TypeToken<List<CancionDetailDTO>>(){}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CancionDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        CancionEntity cancionEntity = cancionService.getCancion(id);
        return modelMapper.map(cancionEntity, CancionDetailDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CancionDTO update(@PathVariable("id") Long id, @RequestBody CancionDTO cancionDTO) throws EntityNotFoundException, IllegalOperationException {
        CancionEntity cancionEntity = cancionService.updateCancion(id, modelMapper.map(cancionDTO, CancionEntity.class)); 
        return modelMapper.map(cancionEntity, CancionDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        cancionService.deleteCancion(id);
    }
    
}
