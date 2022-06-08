package co.edu.uniandes.dse.musica.entities;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;

import uk.co.jemos.podam.common.PodamExclude;


/**
 * Clase que representa una canción en la persistencia
 *
 * @author mar-cas3
 */

@Getter
@Setter
@Entity
public class CancionEntity extends BaseEntity {

    private String titulo;
    private int duracion;
     private String link;
    private String portada;


    @PodamExclude
    @ManyToMany
    Set <AlbumEntity> albumes = new LinkedHashSet<>();
}
