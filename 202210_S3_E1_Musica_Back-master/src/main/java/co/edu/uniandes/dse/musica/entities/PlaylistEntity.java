package co.edu.uniandes.dse.musica.entities;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;

@Getter
@Setter
@Entity

public class PlaylistEntity extends BaseEntity {

	private String nombre;
	private Date fechaCreacion;
	private String imagen;


    @PodamExclude
    @ManyToMany
    private Set<CancionEntity> canciones = new HashSet<CancionEntity>();


}
