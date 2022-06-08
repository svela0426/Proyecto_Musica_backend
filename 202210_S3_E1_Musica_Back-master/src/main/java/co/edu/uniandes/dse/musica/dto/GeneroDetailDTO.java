package co.edu.uniandes.dse.musica.dto;

import java.util.ArrayList;
import java.util.List;

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneroDetailDTO extends GeneroDTO {
	private List<AlbumDTO> albumes = new ArrayList<>();
}
