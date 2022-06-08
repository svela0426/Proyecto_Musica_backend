package co.edu.uniandes.dse.musica.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents the podcast resource DetailDTO.
 *
 * @author Esteban Gonzalez Ruales
 */
@Getter
@Setter
public class PodcastDetailDTO extends PodcastDTO {
    private Set<CreadorDTO> creadores = new HashSet<CreadorDTO>();
    private Set<CapituloDTO> capitulos = new HashSet<CapituloDTO>();
}