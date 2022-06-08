package co.edu.uniandes.dse.musica.dto;

import lombok.Getter; 
import lombok.Setter; 

@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private String nombre; 
    private String login; 
    private String correo; 

    
}
