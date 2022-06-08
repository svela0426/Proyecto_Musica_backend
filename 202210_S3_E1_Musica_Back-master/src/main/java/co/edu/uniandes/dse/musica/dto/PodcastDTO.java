package co.edu.uniandes.dse.musica.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents the podcast resource DTO.
 *
 * @author Esteban Gonzalez Ruales
 */
@Getter
@Setter
public class PodcastDTO {
    private Long id;
    private String titulo;
    private String calificacion;
    private String imagen;
    private String descripcion;
    private double precio;
}