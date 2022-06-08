package co.edu.uniandes.dse.musica.services;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.PlaylistEntity;
import co.edu.uniandes.dse.musica.repositories.CancionRepository;
import co.edu.uniandes.dse.musica.repositories.PlaylistRepository;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;

@Service 
public class PlaylistService {
	
	@Autowired
	PlaylistRepository playlistRepository;
	@Autowired
	CancionRepository cancionRespository; 
	
	@Transactional
	public PlaylistEntity createPlaylist(PlaylistEntity playlist) {
		
		
		return playlistRepository.save(playlist);
	}
	
	@Transactional
	public List<PlaylistEntity> getPlaylists(){
		return playlistRepository.findAll();
	}
	
	@Transactional
	public PlaylistEntity getPlaylist(Long playlistId) throws EntityNotFoundException {
		Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId);
		if (playlistEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);
		return playlistEntity.get();
	}
	@Transactional
	public void deletePlaylist (Long playlistId) throws EntityNotFoundException {
		
		Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId);
		if (playlistEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);
		
		playlistRepository.deleteById(playlistId);
		
	}
	
	@Transactional
	public PlaylistEntity updatePlaylist(Long playlistId, PlaylistEntity playlist) throws EntityNotFoundException {
		Optional<PlaylistEntity> playlistEntity = playlistRepository.findById(playlistId);
		if (playlistEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PLAYLIST_NOT_FOUND);
		
		playlist.setId(playlistId);
		return playlistRepository.save(playlist);
	}

}
