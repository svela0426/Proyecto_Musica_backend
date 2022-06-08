package co.edu.uniandes.dse.musica.entities;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Class that represents a Creador entity.
 *
 * @author Esteban Gonzalez Ruales
 */
@Entity
@Getter
@Setter
public class CreadorEntity extends BaseEntity {

	private String nombre;
	private String nacionalidad;
	private String imagen;

	@PodamExclude
	@ManyToMany
	private Set<AlbumEntity> albumes = new LinkedHashSet<AlbumEntity>();

	@PodamExclude
	@ManyToMany(cascade = CascadeType.PERSIST)
	private Set<PodcastEntity> podcasts = new HashSet<PodcastEntity>();

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public Set<AlbumEntity> getAlbumes() {
		return albumes;
	}

	public void setAlbumes(Set<AlbumEntity> albumes) {
		this.albumes = albumes;
	}

	public Set<PodcastEntity> getPodcasts() {
		return podcasts;
	}

	public void setPodcasts(Set<PodcastEntity> podcasts) {
		this.podcasts = podcasts;
	}
}
