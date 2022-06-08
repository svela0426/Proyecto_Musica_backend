package co.edu.uniandes.dse.musica.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;

import uk.co.jemos.podam.common.PodamExclude;

/**
 * Clase que representa un genero entidad
 *
 * @author juancamilobonet2
 */

@Getter
@Setter
@Entity
public class GeneroEntity extends BaseEntity{
	
	private String nombre;
	
	@PodamExclude
	@ManyToMany
	private List<AlbumEntity> albumes = new ArrayList<>();
	
}
