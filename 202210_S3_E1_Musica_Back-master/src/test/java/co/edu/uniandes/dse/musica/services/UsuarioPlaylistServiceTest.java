package co.edu.uniandes.dse.musica.services;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import co.edu.uniandes.dse.musica.entities.PlaylistEntity;
import co.edu.uniandes.dse.musica.entities.UsuarioEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(UsuarioPlaylistService.class)
public class UsuarioPlaylistServiceTest {
	
	    @Autowired
	    private UsuarioPlaylistService usuarioPlaylistService;

	    @Autowired

	    private TestEntityManager entityManager;

	    private PodamFactory factory = new PodamFactoryImpl();

	    private UsuarioEntity usuario = new UsuarioEntity();

	    private List<PlaylistEntity> playList = new ArrayList<>();
	    
	    
	    
	    @BeforeEach
	    void setUp() {
	        clearData();
	        insertData();
	    }

	    private void clearData() {
	        entityManager.getEntityManager().createQuery("delete from PlaylistEntity").executeUpdate();
	        entityManager.getEntityManager().createQuery("delete from UsuarioEntity").executeUpdate();
	    }

	    private void insertData() {

	    	usuario = factory.manufacturePojo(UsuarioEntity.class);
	        entityManager.persist(usuario);

	        for (int i = 0; i < 3; i++) {
	        	 PlaylistEntity entity = factory.manufacturePojo(PlaylistEntity.class);
	             entityManager.persist(entity);
	             playList.add(entity);
	             usuario.getPlaylists().add(entity);
	         }
	        }
	    
	
	    
	    
	    @Test
	    void testAddPlaylist() throws EntityNotFoundException, IllegalOperationException {
	        UsuarioEntity newUsuario = factory.manufacturePojo(UsuarioEntity.class);
	        entityManager.persist(newUsuario);

	        PlaylistEntity playlist = factory.manufacturePojo(PlaylistEntity.class);
	        entityManager.persist(playlist);

	        usuarioPlaylistService.addPlaylist(playlist.getId(), newUsuario.getId());

	        PlaylistEntity lastPlaylist = usuarioPlaylistService.getPlaylist(playlist.getId(), newUsuario.getId());

	        assertEquals(playlist.getId(), lastPlaylist.getId());
	        assertEquals(playlist.getNombre(), lastPlaylist.getNombre());
	        assertEquals(playlist.getImagen(), lastPlaylist.getImagen());
	        assertEquals(playlist.getFechaCreacion(), lastPlaylist.getFechaCreacion());

	        
	    }
	    
	    
	    @Test
	    void testAddPlaylistInvalidUsuario() {
	        assertThrows(EntityNotFoundException.class, () -> {
	        	PlaylistEntity playlist = factory.manufacturePojo(PlaylistEntity.class);
	            entityManager.persist(playlist);
	            usuarioPlaylistService.addPlaylist(playlist.getId(), 0L);
	        });
	    }
	    
	    
	    @Test
	    void testGetPlaylists() throws EntityNotFoundException {
	    	List<PlaylistEntity> playlistEntities = usuarioPlaylistService.getPlaylists(usuario.getId());

	        assertEquals(playList.size(), playlistEntities.size());

	        for (int i = 0; i < playList.size(); i++) {
	            assertTrue(playlistEntities.contains(playList.iterator().next()));
	        }
	    }
	    
	    @Test
	    void testGetPlaylistWithInvalidUsuario() {
	        assertThrows(EntityNotFoundException.class, () -> {
	        	usuarioPlaylistService.getPlaylists(0L);
	        });
	    }
	    
	    
	    
	    
	    @Test
	    void testGetPlaylist() throws EntityNotFoundException, IllegalOperationException {
	    	PlaylistEntity playlistEntity = playList.iterator().next();
	    	PlaylistEntity playlist = usuarioPlaylistService.getPlaylist(playlistEntity.getId(), usuario.getId());
	        assertNotNull(playlist);

	        assertEquals(playlistEntity.getId(), playlist.getId());
	        assertEquals(playlistEntity.getNombre(), playlist.getNombre());
	        assertEquals(playlistEntity.getFechaCreacion(), playlist.getFechaCreacion());
	        assertEquals(playlistEntity.getImagen(), playlist.getImagen());
	    }
	    
	    @Test
	    void testGetInvalidPlaylist() {
	        assertThrows(EntityNotFoundException.class, () -> {
	        	usuarioPlaylistService.getPlaylist(0L, usuario.getId());
	        });
	    }

	    @Test
	    void testGetPlaylistInvalidUsuario() {
	        assertThrows(EntityNotFoundException.class, () -> {
	            PlaylistEntity playlistEntity = playList.iterator().next();
	            usuarioPlaylistService.getPlaylist(playlistEntity.getId(), 0L);
	        });
	    }
	    
	    
	    @Test
	    void testGetNotAssociatedPlaylist() {
	        assertThrows(IllegalOperationException.class, () -> {
	        	UsuarioEntity newCreador = factory.manufacturePojo(UsuarioEntity.class);
	            entityManager.persist(newCreador);
	            PlaylistEntity album = factory.manufacturePojo(PlaylistEntity.class);
	            entityManager.persist(album);
	            usuarioPlaylistService.getPlaylist(album.getId(), newCreador.getId());
	        });
	    }
	    
	    
	    @Test
	    void testReplacePlaylists() throws EntityNotFoundException {
	    	List<PlaylistEntity> nuevaLista = new  ArrayList<>();
	        for (int i = 0; i < 3; i++) {
	        	PlaylistEntity entity = factory.manufacturePojo(PlaylistEntity.class);
	            entityManager.persist(entity);
	            usuario.getPlaylists().add(entity);
	            nuevaLista.add(entity);
	        }
	        usuarioPlaylistService.replacePlaylists(usuario.getId(), nuevaLista);

	        List<PlaylistEntity>  playlistEntities = usuarioPlaylistService.getPlaylists(usuario.getId());
	        for (PlaylistEntity aNuevaLista : nuevaLista) {
	            assertTrue(playlistEntities.contains(aNuevaLista));
	        }
	    }

	    @Test
	    void testReplacePlaylists2() throws EntityNotFoundException {
	    	List<PlaylistEntity> nuevaLista = new  ArrayList<>();
	        for (int i = 0; i < 3; i++) {
	        	PlaylistEntity entity = factory.manufacturePojo(PlaylistEntity.class);
	            entityManager.persist(entity);
	            nuevaLista.add(entity);
	        }
	        usuarioPlaylistService.replacePlaylists(usuario.getId(), nuevaLista);

	        List<PlaylistEntity>  playlistEntities = usuarioPlaylistService.getPlaylists(usuario.getId());
	        for (PlaylistEntity aNuevaLista : nuevaLista) {
	            assertTrue(playlistEntities.contains(aNuevaLista));
	        }
	    }
	    
	    
	    @Test
	    void testReplaceInvalidPlaylists() {
	        assertThrows(EntityNotFoundException.class, () -> {
		    	List<PlaylistEntity> nuevaLista = new  ArrayList<>();
		    	PlaylistEntity entity = factory.manufacturePojo(PlaylistEntity.class);
	            entity.setId(0L);
	            nuevaLista.add(entity);
	            usuarioPlaylistService.replacePlaylists(0L, nuevaLista);
	        });
	    }
	    
	    
	

	    @Test
	    void testRemovePlaylist() throws EntityNotFoundException {
	        for (PlaylistEntity album : playList) {
	        	usuarioPlaylistService.removePlaylist(album.getId(), usuario.getId());
	        }

	        assertTrue(usuarioPlaylistService.getPlaylists(usuario.getId()).isEmpty());
	    }
	    
	    
	    @Test
	    void removeInvalidPlaylist() {
	        assertThrows(EntityNotFoundException.class, () -> {
	        	usuarioPlaylistService.removePlaylist(0L, usuario.getId());
	        });
	    }
	    
	    
	    @Test
	    void testRemovePlaylistInvalidUsuario() {
	        assertThrows(EntityNotFoundException.class, () -> {
	        	usuarioPlaylistService.removePlaylist(playList.iterator().next().getId(), 0L);
	        });
	    }

	    
	    
	    
	    
	    
}
