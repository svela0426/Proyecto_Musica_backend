package co.edu.uniandes.dse.musica.dto;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlbumDetailDTO extends AlbumDTO{

    private Set<CreadorDTO> artistas = new LinkedHashSet<>();
    private Set<CancionDTO> canciones = new LinkedHashSet<>(); 
    private Set<GeneroDTO> generos = new LinkedHashSet<>();
}
