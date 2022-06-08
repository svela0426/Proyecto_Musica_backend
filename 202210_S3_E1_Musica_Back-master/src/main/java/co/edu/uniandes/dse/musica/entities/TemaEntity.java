package co.edu.uniandes.dse.musica.entities;


import java.util.ArrayList;
import java.util.List;


import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;


/**
 * Class that represents a tema entity.
 *
 * @author Andres Parraga
 */
@Entity
@Getter
@Setter
public class TemaEntity extends BaseEntity {
    
	private String nombre;
    
    @PodamExclude
    @ManyToMany
    private List<PodcastEntity> podcasts = new ArrayList<>(); 


}
