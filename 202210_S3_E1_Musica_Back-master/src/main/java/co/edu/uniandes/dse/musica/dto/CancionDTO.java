package co.edu.uniandes.dse.musica.dto;

import lombok.Getter; 
import lombok.Setter; 

@Getter
@Setter
public class CancionDTO {
    private Long id;
    private String titulo; 
    private Integer duracion; 
     private String link;
    private String portada;
    
}
