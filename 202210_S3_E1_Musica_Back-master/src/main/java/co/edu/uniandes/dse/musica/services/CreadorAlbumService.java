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
public class CreadorAlbumService {

    @Autowired
    private CreadorRepository creadorRepository; 

    @Autowired
    private AlbumRepository albumRepository; 

    // @Autowired
    // private AlbumCreadorService albumCreadorService; 

    @Transactional
    public AlbumEntity addAlbum(Long idAlbum, Long idCreador) throws EntityNotFoundException, IllegalOperationException {

        Optional<AlbumEntity> albumEntity = albumRepository.findById(idAlbum);
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND); 

        Optional<CreadorEntity> creadorEntity = creadorRepository.findById(idCreador);
        if(creadorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND);
        
        // Check no repeated album name in one creador
        for(AlbumEntity album: creadorEntity.get().getAlbumes()) {
            if(album.getTitulo().equals(albumEntity.get().getTitulo()))
                throw new IllegalOperationException("Album can't have more than 1 creador with same name");
        } 
            
        creadorEntity.get().getAlbumes().add(albumEntity.get());
        albumEntity.get().getArtistas().add(creadorEntity.get());
        creadorRepository.save(creadorEntity.get()); 
        albumRepository.save(albumEntity.get());
        return albumEntity.get();
    }

    @Transactional
    public Set<AlbumEntity> getAlbumes(Long creadorId) throws EntityNotFoundException {
        Optional <CreadorEntity> creadorEntity = creadorRepository.findById(creadorId); 
        if(creadorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CAPITULO_NOT_FOUND); 
        return creadorEntity.get().getAlbumes();  
    }

    @Transactional
    public AlbumEntity getAlbum(Long albumId, Long creadorId) throws EntityNotFoundException, IllegalOperationException {
        Optional<CreadorEntity> creadorEntity = creadorRepository.findById(creadorId); 
        if(creadorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND);
        
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId);
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
        
        if(creadorEntity.get().getAlbumes().contains(albumEntity.get()))
            return albumEntity.get(); 

        throw new IllegalOperationException("The song is not associated with the album");
    }

    @Transactional
    public Set<AlbumEntity> replaceAlbumes(Long creadorId, Set<AlbumEntity> listAlbum) throws EntityNotFoundException {
        Optional<CreadorEntity> creadorEntity = creadorRepository.findById(creadorId);
        if(creadorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND);
        
        for(AlbumEntity album: listAlbum ) {
            Optional<AlbumEntity> albumEntity = albumRepository.findById(album.getId());
            if(albumEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
            
            if(!creadorEntity.get().getAlbumes().contains(albumEntity.get()))
                creadorEntity.get().getAlbumes().add(albumEntity.get()); 
        }

        return getAlbumes(creadorId); 
    }

    @Transactional
    public void removeAlbum(Long albumId, Long creadorId) throws EntityNotFoundException {
        Optional<CreadorEntity> creadorEntity = creadorRepository.findById(creadorId); 
        if(creadorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.CREADOR_NOT_FOUND); 
        
        Optional<AlbumEntity> albumEntity = albumRepository.findById(albumId); 
        if(albumEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ALBUM_NOT_FOUND);
        
        creadorEntity.get().getAlbumes().remove(albumEntity.get());
        albumEntity.get().getArtistas().remove(creadorEntity.get());
        
    }
    
}
