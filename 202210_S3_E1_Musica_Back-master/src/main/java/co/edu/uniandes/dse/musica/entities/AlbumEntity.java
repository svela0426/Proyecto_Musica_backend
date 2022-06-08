package co.edu.uniandes.dse.musica.entities;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;

import uk.co.jemos.podam.common.PodamExclude;

/**
 * Clase que representa un album en la persistencia
 *
 * @author mar-cas3
 */

 @Getter
 @Setter
 @Entity
 public class AlbumEntity extends BaseEntity {

    private String imagen;
    private String titulo;

    @PodamExclude
    @ManyToMany(mappedBy = "albumes")
    private Set<CreadorEntity> artistas = new LinkedHashSet<>();

    @PodamExclude
    @ManyToMany()
    private Set<GeneroEntity> generos = new LinkedHashSet<>();

    @PodamExclude
    @ManyToMany
    private Set<CancionEntity> canciones = new LinkedHashSet<>();


 }