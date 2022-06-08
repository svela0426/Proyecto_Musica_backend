package co.edu.uniandes.dse.musica.dto;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents the creador resource DetailDTO.
 *
 * @author Esteban Gonzalez Ruales
 */
@Getter
@Setter
public class CreadorDetailDTO extends CreadorDTO {
    private Set<AlbumDTO> albumes = new LinkedHashSet<AlbumDTO>();
    private Set<PodcastDTO> podcasts = new HashSet<PodcastDTO>();
}