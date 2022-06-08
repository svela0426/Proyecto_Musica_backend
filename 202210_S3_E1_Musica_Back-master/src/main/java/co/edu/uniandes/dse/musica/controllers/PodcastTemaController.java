package co.edu.uniandes.dse.musica.controllers;

import java.util.List;
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

import co.edu.uniandes.dse.musica.dto.PodcastDTO;
import co.edu.uniandes.dse.musica.dto.PodcastDetailDTO;
import co.edu.uniandes.dse.musica.dto.TemaDetailDTO;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.entities.TemaEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.PodcastTemaService;
import co.edu.uniandes.dse.musica.services.TemaPodcastService;

/**
 * Clase que implementa el recurso "temas/{id}/podcasts".
 *
 * @tema ISIS2603
 */
@RestController
@RequestMapping("/podcasts")
public class PodcastTemaController {

	@Autowired
	private PodcastTemaService podcastTemaService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping(value = "/{podcastId}/temas/{temaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public TemaDetailDTO addTema(@PathVariable("temaId") Long temaId, @PathVariable("podcastId") Long podcastId)
			throws EntityNotFoundException, IllegalOperationException {
		TemaEntity temaEntity = podcastTemaService.addTema(podcastId, temaId);
		return modelMapper.map(temaEntity, TemaDetailDTO.class);
	}

	@GetMapping(value = "/{podcastId}/temas/{temaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public TemaDetailDTO getTema(@PathVariable("podcastId") Long podcastId, @PathVariable("temaId") Long temaId)
			throws EntityNotFoundException, IllegalOperationException {
		TemaEntity temaEntity = podcastTemaService.getTema(podcastId, temaId);
		return modelMapper.map(temaEntity, TemaDetailDTO.class);
	}

	@GetMapping(value = "/{podcastId}/temas")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<TemaDetailDTO> getTemas(@PathVariable("podcastId") Long podcastId) throws EntityNotFoundException {
		Set<TemaEntity> temaEntity = podcastTemaService.getTemas(podcastId);
		return modelMapper.map(temaEntity, new TypeToken<Set<TemaDetailDTO>>() {
		}.getType());
	}

	
	@PutMapping(value = "/{podcastId}/temas")
	@ResponseStatus(code = HttpStatus.OK)
	public Set<TemaDetailDTO> replaceTemas(@PathVariable("podcastId") Long podcastId, @RequestBody List<TemaEntity> podcasts)
			throws EntityNotFoundException, IllegalOperationException {
		List<TemaEntity> entities = modelMapper.map(podcasts, new TypeToken<List<TemaEntity>>() {
		}.getType());
		Set<TemaEntity> temaList = podcastTemaService.replaceTemas(podcastId, entities);
		return modelMapper.map(temaList, new TypeToken<Set<TemaDetailDTO>>() {
		}.getType());

	}

	@DeleteMapping(value = "/{podcastId}/temas/{temaId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeTema(@PathVariable("podcastId") Long podcastId, @PathVariable("temaId") Long temaId)
			throws EntityNotFoundException, IllegalOperationException {
				podcastTemaService.removeTema(podcastId, temaId);
	}
}