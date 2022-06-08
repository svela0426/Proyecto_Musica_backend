package co.edu.uniandes.dse.musica.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter; 
import lombok.Setter; 

@Getter
@Setter
public class TemaDetailDTO extends TemaDTO 
{
    //Lista de podcasts - Non duplicate.
    private Set<PodcastDTO> podcasts = new HashSet<PodcastDTO>(); 
}
