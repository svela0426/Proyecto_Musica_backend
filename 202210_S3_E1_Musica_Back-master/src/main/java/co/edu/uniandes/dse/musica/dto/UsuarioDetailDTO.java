package co.edu.uniandes.dse.musica.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDetailDTO extends UsuarioDTO{
	
	private List<PlaylistDTO> playlists = new ArrayList<>();
	
}