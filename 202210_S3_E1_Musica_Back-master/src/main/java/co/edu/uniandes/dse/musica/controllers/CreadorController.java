package co.edu.uniandes.dse.musica.controllers;

import java.util.List;

import javax.transaction.Transactional;

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
import co.edu.uniandes.dse.musica.services.CreadorService;

/**
 * Class that represents the creador controller.
 *
 * @author Esteban Gonzalez Ruales
 */
@RestController
@RequestMapping("/creadores")
@Transactional
public class CreadorController {

	@Autowired
	private CreadorService creadorService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	@Transactional
	public CreadorDTO create(@RequestBody CreadorDTO creadorDTO)
			throws IllegalOperationException, EntityNotFoundException {
		CreadorEntity creadorEntity = creadorService.createCreador(modelMapper.map(creadorDTO, CreadorEntity.class));
		return modelMapper.map(creadorEntity, CreadorDTO.class);
	}

	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public List<CreadorDetailDTO> findAll() {
		List<CreadorEntity> creadores = creadorService.getCreadores();
		return modelMapper.map(creadores, new TypeToken<List<CreadorDetailDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{creadorId}")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public CreadorDetailDTO findOne(@PathVariable("creadorId") Long creadorId) throws EntityNotFoundException {
		CreadorEntity creadorEntity = creadorService.getCreador(creadorId);
		return modelMapper.map(creadorEntity, CreadorDetailDTO.class);
	}

	@PutMapping(value = "/{creadorId}")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public CreadorDTO update(@PathVariable("creadorId") Long creadorId, @RequestBody CreadorDTO creadorDTO)
			throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creadorEntity = creadorService.updateCreador(creadorId,
				modelMapper.map(creadorDTO, CreadorEntity.class));
		return modelMapper.map(creadorEntity, CreadorDTO.class);
	}

	@DeleteMapping(value = "/{creadorId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@Transactional
	public void delete(@PathVariable("creadorId") Long creadorId)
			throws EntityNotFoundException, IllegalOperationException {
		creadorService.deleteCreador(creadorId);
	}
}