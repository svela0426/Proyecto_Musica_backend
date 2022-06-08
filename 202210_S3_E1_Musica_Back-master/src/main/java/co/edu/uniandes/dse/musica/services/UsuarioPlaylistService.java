package co.edu.uniandes.dse.musica.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.musica.entities.PlaylistEntity;
import co.edu.uniandes.dse.musica.entities.UsuarioEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.repositories.PlaylistRepository;
import co.edu.uniandes.dse.musica.repositories.UsuarioRepository;

@Service
public class UsuarioPlaylistService {

	
	@Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlaylistRepository playlistRepository;
    
    
    
    @Transactional
    public PlaylistEntity addPlaylist (Long playlistId, Long usuarioId) throws EntityNotFoundException {
    	
        java.util.Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId);
    	
    	if (playlistEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);

        java.util.Optional<UsuarioEntity> usuario = usuarioRepository.findById(usuarioId);
        if (usuario.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);

        usuario.get().getPlaylists().add(playlistEntity.get());
        
        return playlistEntity.get();
    }
    
    @Transactional
    public java.util.List<PlaylistEntity> getPlaylists(Long usuarioId) throws EntityNotFoundException {
    	
    	java.util.Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
    	
        if (usuarioEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);
        
        
        return usuarioEntity.get().getPlaylists();
    	
    }
    @Transactional
    public PlaylistEntity getPlaylist(Long playlistId, Long usuarioId)
                    throws EntityNotFoundException, IllegalOperationException {
    		java.util.Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
            java.util.Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId);

            if (usuarioEntity.isEmpty())
                    throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);

            if (playlistEntity.isEmpty())
                    throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);
            if (usuarioEntity.get().getPlaylists().contains(playlistEntity.get()))
                    return playlistEntity.get();

            throw new IllegalOperationException("The author is not associated to the book");
    }
    
    
    
    @Transactional
    public  java.util.List<PlaylistEntity> replacePlaylists(Long usuarioId, List<PlaylistEntity> lista) throws EntityNotFoundException {
    	
    	 java.util.Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
         if (usuarioEntity.isEmpty())
                 throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);

         for (PlaylistEntity playlist : lista) {
                 java.util.Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlist.getId());
                 if (playlistEntity.isEmpty())
                         throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);

                 if (!usuarioEntity.get().getPlaylists().contains(playlistEntity.get()))
                	 usuarioEntity.get().getPlaylists().add(playlistEntity.get());
         }
         return getPlaylists(usuarioId);
    
    }
    
    @Transactional
    public void removePlaylist(Long playlistId, Long usuarioId) throws EntityNotFoundException {
            java.util.Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId);
            java.util.Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);

            if (playlistEntity.isEmpty())
                    throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);

            if (usuarioEntity.isEmpty())
                    throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);

            usuarioEntity.get().getPlaylists().remove(playlistEntity.get());

    }


}
