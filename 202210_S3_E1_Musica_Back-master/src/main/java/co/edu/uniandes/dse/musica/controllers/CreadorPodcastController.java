package co.edu.uniandes.dse.musica.controllers;

import java.util.Set;

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

import co.edu.uniandes.dse.musica.dto.PodcastDTO;
import co.edu.uniandes.dse.musica.dto.PodcastDetailDTO;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.CreadorPodcastService;

/**
* Class to represent association of a creador with its podcasts as a
resource.
*
* @author Esteban Gonzalez Ruales
*/
@RestController
@RequestMapping("/creadores")
@Transactional
public class CreadorPodcastController {

	@Autowired
	private CreadorPodcastService creadorPodcastService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping(value = "/{creadorId}/podcasts/{podcastId}")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public PodcastDetailDTO addPodcastToCreador(@PathVariable("podcastId") Long podcastId, @PathVariable("creadorId") Long creadorId) throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcastEntity = creadorPodcastService.addPodcastToCreador(creadorId, podcastId);
		return modelMapper.map(podcastEntity, PodcastDetailDTO.class);
	}

	@GetMapping(value = "/{creadorId}/podcasts/{podcastId}")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public PodcastDetailDTO getPodcastOfCreador(@PathVariable("podcastId") Long podcastId, @PathVariable("creadorId") Long creadorId) throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcastEntity = creadorPodcastService.getPodcastOfCreador(creadorId, podcastId);
		return modelMapper.map(podcastEntity, PodcastDetailDTO.class);
	}

	@GetMapping(value = "/{creadorId}/podcasts")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public Set<PodcastDetailDTO> getPodcastsOfCreador(@PathVariable("creadorId") Long creadorId) throws EntityNotFoundException {
		Set<PodcastEntity> podcasts = creadorPodcastService.getPodcastsOfCreador(creadorId);
		return modelMapper.map(podcasts, new TypeToken<Set<PodcastDetailDTO>>() {
			}.getType());
	}

	@PutMapping(value = "/{creadorId}/podcasts")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public Set<PodcastDetailDTO> replacePodcastsOfCreador(@PathVariable("creadorId") Long creadorId, @RequestBody Set<PodcastDTO> podcasts) throws EntityNotFoundException, IllegalOperationException {
		Set<PodcastEntity> podcastEntities = modelMapper.map(podcasts, new TypeToken<Set<PodcastEntity>>() {
			}.getType());

		Set<PodcastEntity> replacedPodcasts = creadorPodcastService.replacePodcastsOfCreador(creadorId, podcastEntities);
		return modelMapper.map(replacedPodcasts, new TypeToken<Set<PodcastDetailDTO>>() {
		}.getType());
	}

	@DeleteMapping(value = "/{creadorId}/podcasts/{podcastId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@Transactional
	public void removePodcastOfCreador(@PathVariable("podcastId") Long podcastId, @PathVariable("creadorId") Long creadorId) throws EntityNotFoundException, IllegalOperationException {
		// removeCreadorOfPodcast has to go first
		// podcastCreadorService.removeCreadorOfPodcast(podcastId, creadorId);
		creadorPodcastService.removePodcastOfCreador(creadorId, podcastId);
	}
}