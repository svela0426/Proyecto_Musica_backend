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

import co.edu.uniandes.dse.musica.dto.TemaDTO;
import co.edu.uniandes.dse.musica.dto.TemaDetailDTO;
import co.edu.uniandes.dse.musica.entities.TemaEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.TemaService;

@RestController
@RequestMapping("/temas")
public class TemaController {
	
	@Autowired
	private TemaService temaService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Busca y devuelve todos los temas que existen en la aplicacion.
	 *
	 * @return JSONArray {@link TemaDTODetails} - Los temas encontrados en la
	 *         aplicación. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<TemaDetailDTO> findAll() {
		List<TemaEntity> temas = temaService.getTemas();
		return modelMapper.map(temas, new TypeToken<List<TemaDetailDTO>>() {
		}.getType());
	}
	
	/**
	 * Busca el tema con el id asociado recibido en la URL y lo devuelve.
	 *
	 * @param Id Identificador del genero que se esta buscando. Este debe ser una
	 *               cadena de dígitos.
	 * @return JSON {@link TemaDTODetails} - El tema buscado
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public TemaDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
		TemaEntity temaEntity = temaService.getTema(id);
		return modelMapper.map(temaEntity, TemaDetailDTO.class);
	}
	
	/**
	 * Crea un nuevo tema con la informacion que se recibe en el cuerpo de la
	 * petición y se regresa un objeto identico con un id auto-generado por la base
	 * de datos.
	 *
	 * @param tema {@link TemaDTO} - EL tema que se desea guardar.
	 * @return JSON {@link TemaDTO} - El tema guardado con el atributo id
	 *         autogenerado.
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public TemaDTO create(@RequestBody TemaDTO temaDTO) throws IllegalOperationException, EntityNotFoundException {
		TemaEntity temaEntity = temaService.createtema(modelMapper.map(temaDTO, TemaEntity.class));
		return modelMapper.map(temaEntity, TemaDTO.class);
	}

	/**
	 * Actualiza el genero con el id recibido en la URL con la información que se
	 * recibe en el cuerpo de la petición.
	 *
	 * @param temaId Identificador del genero que se desea actualizar. Este debe ser
	 *               una cadena de dígitos.
	 * @param tema   {@link temaDTO} El genero que se desea guardar.
	 * @return JSON {@link temaDTO} - El genero guardada.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public TemaDTO update(@PathVariable("id") Long id, @RequestBody TemaDTO temaDTO)
			throws EntityNotFoundException, IllegalOperationException {
		TemaEntity temaEntity = temaService.updateTema(id, modelMapper.map(temaDTO, TemaEntity.class));
		return modelMapper.map(temaEntity, TemaDTO.class);
	}

	/**
	 * Borra el tema con el id asociado recibido en la URL.
	 *
	 * @param generoId Identificador del genero que se desea borrar. Este debe ser una
	 *               cadena de dígitos.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
		temaService.deleteTema(id);
	}
	
}