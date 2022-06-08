package co.edu.uniandes.dse.musica.dto;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancionDetailDTO extends CancionDTO {

    private Set<AlbumDTO> albumes = new LinkedHashSet<>(); 
    
}
