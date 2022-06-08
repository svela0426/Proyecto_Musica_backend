package co.edu.uniandes.dse.musica.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents the creador resource DTO.
 *
 * @author Esteban Gonzalez Ruales
 */
@Getter
@Setter
public class CreadorDTO {
    private Long id;
    private String nombre;
    private String nacionalidad;
    private String imagen;
}