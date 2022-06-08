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
public class CancionAlbumService {
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
    public AlbumEntity addAlbum(Long idAlbum, Long idCancion) throws EntityNotFoundException, IllegalOperationException {

        Optional<AlbumEntity> albumEntity = albumRepository.findById(idAlbum);
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND); 

        Optional<CancionEntity> cancionEntity = cancionRepository.findById(idCancion);
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);

        for (AlbumEntity album : cancionEntity.get().getAlbumes()) {
            if (album.getTitulo().equals(albumEntity.get().getTitulo()))
                throw new IllegalOperationException("Album already exists in cancion");
        }
        
        cancionEntity.get().getAlbumes().add(albumEntity.get());
        albumEntity.get().getCanciones().add(cancionEntity.get());
        cancionRepository.save(cancionEntity.get());
        albumRepository.save(albumEntity.get());
        return albumEntity.get();
    }

    @Transactional
    public Set<AlbumEntity> getAlbumes(Long cancionId) throws EntityNotFoundException {
        Optional <CancionEntity> cancionEntity = cancionRepository.findById(cancionId); 
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND); 
        
        return cancionEntity.get().getAlbumes();  
    }

    @Transactional
    public AlbumEntity getAlbum(Long albumId, Long cancionId) throws EntityNotFoundException, IllegalOperationException {
        Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId); 
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
        
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
        
        
        if(cancionEntity.get().getAlbumes().contains(albumEntity.get()))
            return albumEntity.get(); 

        throw new IllegalOperationException("The song is not associated with the album");
    }

    @Transactional
    public Set<AlbumEntity> replaceAlbumes(Long cancionId, Set<AlbumEntity> listAlbum) throws EntityNotFoundException {
        Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId);
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND);
        
        for(AlbumEntity album: listAlbum ) {
            Optional<AlbumEntity> albumEntity = albumRepository.findById(album.getId());
            if(albumEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
            
            if(!cancionEntity.get().getAlbumes().contains(albumEntity.get()))
                cancionEntity.get().getAlbumes().add(albumEntity.get()); 
        }

        return getAlbumes(cancionId); 
    }

    @Transactional
    public void removeAlbum(Long albumId, Long cancionId) throws EntityNotFoundException {
        Optional<CancionEntity> cancionEntity = cancionRepository.findById(cancionId); 
        if(cancionEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CANCION_NOT_FOUND); 
        
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId); 
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
        
        cancionEntity.get().getAlbumes().remove(albumEntity.get());
        albumEntity.get().getCanciones().remove(cancionEntity.get());

    }
}
