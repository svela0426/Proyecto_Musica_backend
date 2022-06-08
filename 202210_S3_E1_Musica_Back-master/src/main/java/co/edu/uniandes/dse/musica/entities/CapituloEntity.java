package co.edu.uniandes.dse.musica.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Class that represents a Capitulo entity.
 *
 * @author Esteban Gonzalez Ruales
 */
@Entity
@Getter
@Setter
public class CapituloEntity extends BaseEntity {

	private String titulo;
	private String imagen;
	private int duracion;
	@Temporal(TemporalType.DATE)
	private Date fechaPublicacion;

	@PodamExclude
	@ManyToOne(cascade = CascadeType.PERSIST)
	private PodcastEntity podcast;

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public Date getFechaPublicacion() {
		return fechaPublicacion;
	}

	public void setFechaPublicacion(Date fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}

	public PodcastEntity getPodcast() {
		return podcast;
	}

	public void setPodcast(PodcastEntity podcast) {
		this.podcast = podcast;
	}
}
