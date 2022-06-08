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

import co.edu.uniandes.dse.musica.dto.PodcastDTO;
import co.edu.uniandes.dse.musica.dto.PodcastDetailDTO;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.PodcastService;

/**
 * Class that represents the podcast controller.
 *
 * @author Esteban Gonzalez Ruales
 */
@RestController
@RequestMapping("/podcasts")
@Transactional
public class PodcastController {

	@Autowired
	private PodcastService podcastService;

	@Autowired
	private ModelMapper modelMapper;

	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	@Transactional
	public PodcastDTO create(@RequestBody PodcastDTO podcastDTO) throws IllegalOperationException, EntityNotFoundException {
		PodcastEntity podcastEntity = podcastService.createPodcast(modelMapper.map(podcastDTO, PodcastEntity.class));
		return modelMapper.map(podcastEntity, PodcastDTO.class);
	}

	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public List<PodcastDetailDTO> findAll() {
		List<PodcastEntity> podcasts = podcastService.getPodcasts();
		return modelMapper.map(podcasts, new TypeToken<List<PodcastDetailDTO>>() {
			}.getType());
	}

	@GetMapping(value = "/{podcastId}")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public PodcastDetailDTO findOne(@PathVariable("podcastId") Long podcastId) throws EntityNotFoundException {
		PodcastEntity podcastEntity = podcastService.getPodcast(podcastId);
		return modelMapper.map(podcastEntity, PodcastDetailDTO.class);
	}

	@PutMapping(value = "/{podcastId}")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public PodcastDTO update(@PathVariable("podcastId") Long podcastId, @RequestBody PodcastDTO podcastDTO) throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcastEntity = podcastService.updatePodcast(podcastId, modelMapper.map(podcastDTO, PodcastEntity.class));
		return modelMapper.map(podcastEntity, PodcastDTO.class);
	}

	@DeleteMapping(value = "/{podcastId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@Transactional
	public void delete(@PathVariable("podcastId") Long podcastId) throws EntityNotFoundException, IllegalOperationException {
		podcastService.deletePodcast(podcastId);
	}
}