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

import co.edu.uniandes.dse.musica.dto.CapituloDTO;
import co.edu.uniandes.dse.musica.dto.CapituloDetailDTO;
import co.edu.uniandes.dse.musica.dto.PodcastDTO;
import co.edu.uniandes.dse.musica.entities.CapituloEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.CapituloService;
import co.edu.uniandes.dse.musica.services.PodcastCapituloService;

/**
 * Class to represent association of a podcast with its capitulos as a resource.
 *
 * @author Esteban Gonzalez Ruales
 */
@RestController
@RequestMapping("/podcasts")
@Transactional
public class PodcastCapituloController {

    @Autowired
    private CapituloService capituloService;

    @Autowired
    private PodcastCapituloService podcastCapituloService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = "/{podcastId}/capitulos")
    @ResponseStatus(code = HttpStatus.CREATED)
    @Transactional
    public CapituloDTO create(@PathVariable("podcastId") Long podcastId, @RequestBody CapituloDTO capituloDTO)
            throws IllegalOperationException, EntityNotFoundException {
        CapituloEntity capituloEntity = capituloService
                .createCapitulo(modelMapper.map(capituloDTO, CapituloEntity.class));
        podcastCapituloService.addCapituloToPodcast(podcastId, capituloEntity.getId());
        return modelMapper.map(capituloEntity, CapituloDTO.class);
    }

    @GetMapping(value = "/{podcastId}/capitulos/{capituloId}")
    @ResponseStatus(code = HttpStatus.OK)
    @Transactional
    public CapituloDTO getCapituloOfPodcast(@PathVariable("capituloId") Long capituloId,
            @PathVariable("podcastId") Long podcastId) throws EntityNotFoundException, IllegalOperationException {
        CapituloEntity capituloEntity = podcastCapituloService.getCapituloOfPodcast(podcastId, capituloId);
        return modelMapper.map(capituloEntity, CapituloDTO.class);
    }

    @GetMapping(value = "/{podcastId}/capitulos")
    @ResponseStatus(code = HttpStatus.OK)
    @Transactional
    public Set<CapituloDetailDTO> getCapitulosOfPodcast(@PathVariable("podcastId") Long podcastId)
            throws EntityNotFoundException {
        Set<CapituloEntity> capitulos = podcastCapituloService.getCapitulosOfPodcast(podcastId);
        return modelMapper.map(capitulos, new TypeToken<Set<CapituloDetailDTO>>() {
        }.getType());
    }

    @PutMapping(value = "/{podcastId}/capitulos")
    @ResponseStatus(code = HttpStatus.OK)
    @Transactional
    public Set<CapituloDetailDTO> replaceCapitulosOfPodcast(@PathVariable("podcastId") Long podcastId,
            @RequestBody Set<PodcastDTO> podcasts) throws EntityNotFoundException, IllegalOperationException {
        Set<CapituloEntity> capituloEntities = modelMapper.map(podcasts, new TypeToken<Set<CapituloEntity>>() {
        }.getType());

        Set<CapituloEntity> replacedCapitulos = podcastCapituloService.replaceCapitulosOfPodcast(podcastId,
                capituloEntities);

        for (CapituloEntity capitulo : replacedCapitulos) {
            capituloService.updateCapitulo(capitulo.getId(), capitulo);
        }

        return modelMapper.map(replacedCapitulos, new TypeToken<Set<CapituloDetailDTO>>() {
        }.getType());
    }

    @DeleteMapping(value = "/{podcastId}/capitulos/{capituloId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Transactional
    public void removeCapituloOfPodcast(@PathVariable("capituloId") Long capituloId,
            @PathVariable("podcastId") Long podcastId) throws EntityNotFoundException, IllegalOperationException {
        podcastCapituloService.removeCapituloOfPodcast(podcastId, capituloId);
        capituloService.deleteCapitulo(capituloId);
    }
}