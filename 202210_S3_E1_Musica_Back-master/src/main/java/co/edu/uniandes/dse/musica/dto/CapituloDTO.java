package co.edu.uniandes.dse.musica.dto;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

/**
 * Class that represents the capitulo resource DTO.
 *
 * @author Esteban Gonzalez Ruales
 */
@Getter
@Setter
public class CapituloDTO {
    private Long id;
	private String titulo;
    private String imagen;
    private int duracion;
    @Temporal(TemporalType.DATE)
    private Date fechaPublicacion;
    private PodcastDTO podcast;
}