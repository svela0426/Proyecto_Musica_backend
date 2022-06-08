package co.edu.uniandes.dse.musica.dto;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PlaylistDTO {
	
	private String nombre;
	private Date fechaCreacion;
	private String imagen;
	private Long id;
	

}
