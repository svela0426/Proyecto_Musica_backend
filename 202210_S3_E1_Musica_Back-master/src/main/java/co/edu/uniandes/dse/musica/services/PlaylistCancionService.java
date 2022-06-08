package co.edu.uniandes.dse.musica.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.PlaylistEntity;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.repositories.PlaylistRepository;
import co.edu.uniandes.dse.musica.repositories.CancionRepository;
@Service
public class PlaylistCancionService {

    @Autowired
    private PlaylistRepository playlistRepository; 
    
    @Autowired
    private CancionRepository cancionRepository;


    /**
    * Asocia una Cancion existente a una Playlist
    *
    * @param playlistId   Identificador de la instancia de la Playlist
    * @param cancionId Identificador de la instancia de Cancion
    * @return Instancia de CancionEntity que fue asociada a la Playlist
     * @throws EntityNotFoundException
     */
    @Transactional
    public CancionEntity addCancion(Long idPlaylist, Long idCancion) throws EntityNotFoundException {

        Optional<CancionEntity> cancionEntity = cancionRepository.findById(idCancion);
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND); 

        Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(idPlaylist);
        if(playlistEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);
        
        playlistEntity.get().getCanciones().add(cancionEntity.get());
        return cancionEntity.get();
    }

    @Transactional
    public Set<CancionEntity> getCanciones(Long playlistId) throws EntityNotFoundException {
        Optional <PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId); 
        if(playlistEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND); 
        
        return playlistEntity.get().getCanciones();  
    }

    @Transactional
    public CancionEntity getCancion(Long playlistId, Long cancionId) throws EntityNotFoundException, IllegalOperationException {
        Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId); 
        if(playlistEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);
        
        Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId);
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
        
        
        if(playlistEntity.get().getCanciones().contains(cancionEntity.get()))
            return cancionEntity.get(); 

        throw new IllegalOperationException("The song is not associated with the playlist");
    }

    @Transactional
    public Set<CancionEntity> replaceCanciones(Long playlistId, Set<CancionEntity> listCancion) throws EntityNotFoundException {
        Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId);
        if(playlistEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);
        
        for(CancionEntity cancion: listCancion ) {
            Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancion.getId());
            if(cancionEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);
            
            if(!playlistEntity.get().getCanciones().contains(cancionEntity.get()))
            	playlistEntity.get().getCanciones().add(cancionEntity.get()); 
        }

        return getCanciones(playlistId); 
    }

    @Transactional
    public void removeCancion(Long playlistId, Long cancionId) throws EntityNotFoundException {
        Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId); 
        if(playlistEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND); 
        
        Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId); 
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
        
        playlistEntity.get().getCanciones().remove(cancionEntity.get());

    }

    
}