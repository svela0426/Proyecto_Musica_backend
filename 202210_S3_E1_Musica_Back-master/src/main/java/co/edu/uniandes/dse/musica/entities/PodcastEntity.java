package co.edu.uniandes.dse.musica.entities;

import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;


/**
 * Class that represents a Podcast entity.
 *
 * @author Esteban Gonzalez Ruales
 */
@Entity
@Getter
@Setter
public class PodcastEntity extends BaseEntity {

	private String titulo;
    private String calificacion;
    private String imagen;
    private String descripcion;
    private double precio;

    @PodamExclude
    @ManyToMany
    private Set<CreadorEntity> creadores = new HashSet<CreadorEntity>();


    @PodamExclude
    @OneToMany(
        mappedBy = "podcast",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL
    )
    private Set<CapituloEntity> capitulos = new HashSet<CapituloEntity>();

	@PodamExclude
    @ManyToMany()
    private Set<TemaEntity> temas = new LinkedHashSet<>();

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(String calificacion) {
		this.calificacion = calificacion;
	}

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public Set<CreadorEntity> getCreadores() {
		return creadores;
	}

	public void setCreadores(Set<CreadorEntity> creadores) {
		this.creadores = creadores;
	}

	public Set<CapituloEntity> getCapitulos() {
		return capitulos;
	}

	public void setCapitulos(Set<CapituloEntity> capitulos) {
		this.capitulos = capitulos;
	}

}
