package co.edu.uniandes.dse.musica.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;


import uk.co.jemos.podam.common.PodamExclude;


/**
 * Clase que representa un Usuario
 *
 * @author svela0426
 */

@Getter
@Setter
@Entity
public class UsuarioEntity extends BaseEntity {

    private String nombre; 
    private String login;
    private String correo; 


	@PodamExclude
    @OneToMany		
    private List <PlaylistEntity> playlists = new ArrayList<>();
    
    

    

}
