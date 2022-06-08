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
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;

import co.edu.uniandes.dse.musica.services.TemaPodcastService;

/**
 * Clase que implementa el recurso "temas/{id}/podcasts".
 *
 * @tema ISIS2603
 */
@RestController
@RequestMapping("/temas")
public class TemaPodcastController {

	@Autowired
	private TemaPodcastService temaPodcastService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping(value = "/{temaId}/podcasts/{podcastId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PodcastDetailDTO addPodcast(@PathVariable("temaId") Long temaId, @PathVariable("podcastId") Long podcastId)
			throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcastEntity = temaPodcastService.addPodcast(temaId, podcastId);
		return modelMapper.map(podcastEntity, PodcastDetailDTO.class);
	}

	@GetMapping(value = "/{temaId}/podcasts/{podcastId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PodcastDetailDTO getPodcast(@PathVariable("temaId") Long temaId, @PathVariable("podcastId") Long podcastId)
			throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcastEntity = temaPodcastService.getPodcast(temaId, podcastId);
		return modelMapper.map(podcastEntity, PodcastDetailDTO.class);
	}

	@GetMapping(value = "{temaId}/podcasts")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PodcastDetailDTO> getPodcasts(@PathVariable("temaId") Long temaId) throws EntityNotFoundException {
		List<PodcastEntity> podcastEntity = temaPodcastService.getPodcasts(temaId);
		return modelMapper.map(podcastEntity, new TypeToken<List<PodcastDetailDTO>>() {
		}.getType());
	}

	
	@PutMapping(value = "{temaId}/podcasts")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PodcastDetailDTO> replacePodcasts(@PathVariable("temaId") Long podcastId, @RequestBody List<PodcastDTO> podcasts)
			throws EntityNotFoundException, IllegalOperationException {
		List<PodcastEntity> entities = modelMapper.map(podcasts, new TypeToken<List<PodcastEntity>>() {
		}.getType());
		List<PodcastEntity> podcastList = temaPodcastService.replacePodcasts(podcastId, entities);
		return modelMapper.map(podcastList, new TypeToken<List<PodcastDetailDTO>>() {
		}.getType());

	}

	/**
	 * Elimina la conexión entre el libro y e autor recibidos en la URL.
	 *
	 * @param temaId El ID del tema al cual se le va a desasociar el podcast
	 * @param podcastId   El ID del podcast que se desasocia
	 */
	@DeleteMapping(value = "{temaId}/podcasts/{podcastId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removePodcast(@PathVariable("temaId") Long temaId, @PathVariable("podcastId") Long podcastId)
			throws EntityNotFoundException {
		temaPodcastService.removePodcast(temaId, podcastId);
	}
}