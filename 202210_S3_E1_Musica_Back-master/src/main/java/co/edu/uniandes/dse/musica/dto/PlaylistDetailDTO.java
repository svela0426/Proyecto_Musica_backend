package co.edu.uniandes.dse.musica.dto;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class PlaylistDetailDTO extends PlaylistDTO {
	
	private List<CancionDTO> canciones = new ArrayList<>();

}
