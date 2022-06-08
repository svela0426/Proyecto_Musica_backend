package co.edu.uniandes.dse.musica.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.repositories.AlbumRepository;
import co.edu.uniandes.dse.musica.repositories.CreadorRepository;

@Service
public class AlbumCreadorService {

    @Autowired
    private AlbumRepository albumRepository;
        
    @Autowired
    private CreadorRepository creadorRepository; 
    
    // @Autowired
    // private CreadorAlbumService creadorAlbumService;
    /**
    * Asocia una Creador existente a un Album
    *
    * @param albumId   Identificador de la instancia de Album
    * @param creadorId Identificador de la instancia de Creador
    * @return Instancia de CreadorEntity que fue asociada a Album
     * @throws EntityNotFoundException
     * @throws IllegalOperationException
     */
    @Transactional
    public CreadorEntity addCreador(Long idAlbum, Long idCreador) throws EntityNotFoundException, IllegalOperationException {

        Optional<CreadorEntity> creadorEntity = creadorRepository.findById(idCreador);
        if(creadorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND); 

        Optional<AlbumEntity> albumEntity = albumRepository.findById(idAlbum);
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);

        // Check no repeated album name in one creador
        for(AlbumEntity album: creadorEntity.get().getAlbumes()) {
            if(album.getTitulo().equals(albumEntity.get().getTitulo()))
                throw new IllegalOperationException("Creador cant't have more than 1 album with same title");
        } 

        albumEntity.get().getArtistas().add(creadorEntity.get());
        creadorEntity.get().getAlbumes().add(albumEntity.get());
        creadorRepository.save(creadorEntity.get()); 
        albumRepository.save(albumEntity.get()); 
        return creadorEntity.get();
    }

    @Transactional
    public Set<CreadorEntity> getArtistas(Long albumId) throws EntityNotFoundException {
        Optional <AlbumEntity> albumEntity = albumRepository.findById(albumId); 
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND); 
        
        return albumEntity.get().getArtistas();  
    }

    @Transactional
    public CreadorEntity getCreador(Long albumId, Long creadorId) throws EntityNotFoundException, IllegalOperationException {
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId); 
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
        
        Optional<CreadorEntity> creadorEntity = creadorRepository.findById(creadorId);
        if(creadorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND);
        
        
        if(albumEntity.get().getArtistas().contains(creadorEntity.get()))
            return creadorEntity.get(); 

        throw new IllegalOperationException("The song is not associated with the album");
    }

    @Transactional
    public Set<CreadorEntity> replaceArtistas(Long albumId, Set<CreadorEntity> listCreador) throws EntityNotFoundException, IllegalOperationException {
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
        
        for(CreadorEntity creador: listCreador ) {
            Optional<CreadorEntity> creadorEntity = creadorRepository.findById(creador.getId());
            if(creadorEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND);
            
            if(!albumEntity.get().getArtistas().contains(creadorEntity.get())) {
                addCreador(albumEntity.get().getId(), creadorEntity.get().getId());
            }
        }

        return getArtistas(albumId); 
    }

    @Transactional
    public void removeCreador(Long albumId, Long creadorId) throws EntityNotFoundException {
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId); 
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND); 
        
        Optional<CreadorEntity> creadorEntity = creadorRepository.findById(creadorId); 
        if(creadorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND);
        
        albumEntity.get().getArtistas().remove(creadorEntity.get());
        creadorEntity.get().getAlbumes().remove(albumEntity.get());

    }
}
