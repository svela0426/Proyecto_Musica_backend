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

import co.edu.uniandes.dse.musica.dto.CreadorDetailDTO;
import co.edu.uniandes.dse.musica.dto.PodcastDTO;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.PodcastCreadorService;

/**
* Class to represent association of a podcast with its creadores as a
resource.
*
* @author Esteban Gonzalez Ruales
*/
@RestController
@RequestMapping("/podcasts")
@Transactional
public class PodcastCreadorController {

	@Autowired
	private PodcastCreadorService podcastCreadorService;

	@Autowired
	private ModelMapper modelMapper;

	private Long podcastId;

	@PostMapping(value = "/{podcastId}/creadores/{creadorId}")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public CreadorDetailDTO addCreadorToPodcast(@PathVariable("creadorId") Long creadorId, @PathVariable("podcastId") Long podcastId) throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creadorEntity = podcastCreadorService.addCreadorToPodcast(podcastId, creadorId);
		return modelMapper.map(creadorEntity, CreadorDetailDTO.class);
	}

	@GetMapping(value = "/{podcastId}/creadores/{creadorId}")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public CreadorDetailDTO getCreadorOfPodcast(@PathVariable("creadorId") Long creadorId, @PathVariable("podcastId") Long podcastId) throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creadorEntity = podcastCreadorService.getCreadorOfPodcast(podcastId, creadorId);
		return modelMapper.map(creadorEntity, CreadorDetailDTO.class);
	}

	@GetMapping(value = "/{podcastId}/creadores")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public Set<CreadorDetailDTO> getCreadoresOfPodcast(@PathVariable("podcastId") Long podcastId) throws EntityNotFoundException {
		this.podcastId = podcastId;
		Set<CreadorEntity> creadores = podcastCreadorService.getCreadoresOfPodcast(this.podcastId);
		return modelMapper.map(creadores, new TypeToken<Set<CreadorDetailDTO>>() {
		}.getType());
	}

	@PutMapping(value = "/{podcastId}/creadores")
	@ResponseStatus(code = HttpStatus.OK)
	@Transactional
	public Set<CreadorDetailDTO> replaceCreadoresOfPodcast(@PathVariable("podcastId") Long podcastId, @RequestBody Set<PodcastDTO> podcasts) throws EntityNotFoundException, IllegalOperationException {
		Set<CreadorEntity> creadorEntities = modelMapper.map(podcasts, new TypeToken<Set<CreadorEntity>>() {
		}.getType());

		Set<CreadorEntity> replacedCreadores = podcastCreadorService.replaceCreadoresOfPodcast(podcastId, creadorEntities);
		return modelMapper.map(replacedCreadores, new TypeToken<Set<CreadorDetailDTO>>() {
		}.getType());
	}

	@DeleteMapping(value = "/{podcastId}/creadores/{creadorId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	@Transactional
	public void removeCreadorOfPodcast(@PathVariable("creadorId") Long creadorId, @PathVariable("podcastId") Long podcastId) throws EntityNotFoundException, IllegalOperationException {
		podcastCreadorService.removeCreadorOfPodcast(podcastId, creadorId);
	}
}