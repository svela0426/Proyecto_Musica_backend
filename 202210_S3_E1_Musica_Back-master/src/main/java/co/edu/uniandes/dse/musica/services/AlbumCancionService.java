package co.edu.uniandes.dse.musica.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.repositories.AlbumRepository;
import co.edu.uniandes.dse.musica.repositories.CancionRepository;
@Service
public class AlbumCancionService {

    @Autowired
    private AlbumRepository albumRepository; 
    
    @Autowired
    private CancionRepository cancionRepository;


    /**
    * Asocia una Cancion existente a un Album
    *
    * @param albumId   Identificador de la instancia de Album
    * @param cancionId Identificador de la instancia de Cancion
    * @return Instancia de CancionEntity que fue asociada a Album
     * @throws EntityNotFoundException
     * @throws IllegalOperationException
     */
    @Transactional
    public CancionEntity addCancion(Long idAlbum, Long idCancion) throws EntityNotFoundException, IllegalOperationException {

        Optional<CancionEntity> cancionEntity = cancionRepository.findById(idCancion);
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND); 

        Optional<AlbumEntity> albumEntity = albumRepository.findById(idAlbum);
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

        for (CancionEntity cancion : albumEntity.get().getCanciones()) {
			if (cancion.getTitulo().equals(cancionEntity.get().getTitulo()))
				throw new IllegalOperationException("Cancion already exists in album");
		}

        albumEntity.get().getCanciones().add(cancionEntity.get());
        cancionEntity.get().getAlbumes().add(albumEntity.get());
        albumRepository.save(albumEntity.get());
        cancionRepository.save(cancionEntity.get());
        return cancionEntity.get();
    }

    @Transactional
    public Set<CancionEntity> getCanciones(Long albumId) throws EntityNotFoundException {
        Optional <AlbumEntity> albumEntity = albumRepository.findById(albumId); 
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND); 
        
        return albumEntity.get().getCanciones();  
    }

    @Transactional
    public CancionEntity getCancion(Long albumId, Long cancionId) throws EntityNotFoundException, IllegalOperationException {
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId); 
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
        
        Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId);
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
        
        if(albumEntity.get().getCanciones().contains(cancionEntity.get()))
            return cancionEntity.get(); 

        throw new IllegalOperationException("The song is not associated with the album");
    }

    @Transactional
    public Set<CancionEntity> replaceCanciones(Long albumId, Set<CancionEntity> listCancion) throws EntityNotFoundException, IllegalOperationException {
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
        
        for(CancionEntity cancion: listCancion ) {
            Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancion.getId());
            if(cancionEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
            
            if(!albumEntity.get().getCanciones().contains(cancionEntity.get())) {
                addCancion(albumEntity.get().getId(), cancion.getId());
            }
        }

        return getCanciones(albumId); 
    }

    @Transactional
    public void removeCancion(Long albumId, Long cancionId) throws EntityNotFoundException {
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId); 
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND); 
        
        Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId); 
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
        
        albumEntity.get().getCanciones().remove(cancionEntity.get());
        cancionEntity.get().getAlbumes().remove(albumEntity.get()); 

    }

    
}
