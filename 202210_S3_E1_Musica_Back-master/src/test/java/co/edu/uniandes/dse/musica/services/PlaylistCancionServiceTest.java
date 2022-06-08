package co.edu.uniandes.dse.musica.services;
import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashSet;
import java.util.Set;

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
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PlaylistCancionService.class)
class PlaylistCancionServiceTest  {

	@Autowired
	private PlaylistCancionService playlistCancionService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PlaylistEntity playlist = new PlaylistEntity();
	private Set<CancionEntity> cancionList = new LinkedHashSet<>();

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PlaylistEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from CancionEntity").executeUpdate();
	}

	private void insertData() {

		playlist = factory.manufacturePojo(PlaylistEntity.class);
		entityManager.persist(playlist);

		for (int i = 0; i < 3; i++) {
			CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(entity);			
			cancionList.add(entity);
			playlist.getCanciones().add(entity);
		}
	}

	@Test
	void testAddCancion() throws EntityNotFoundException, IllegalOperationException {
		PlaylistEntity newPlaylist = factory.manufacturePojo(PlaylistEntity.class);
		entityManager.persist(newPlaylist);

		CancionEntity cancion = factory.manufacturePojo(CancionEntity.class);
		entityManager.persist(cancion);

		playlistCancionService.addCancion(newPlaylist.getId(), cancion.getId());

		CancionEntity lastCancion = playlistCancionService.getCancion(newPlaylist.getId(), cancion.getId());

		assertEquals(cancion.getId(), lastCancion.getId());
		assertEquals(cancion.getTitulo(), lastCancion.getTitulo());
		assertEquals(cancion.getDuracion(), lastCancion.getDuracion());
	}

	@Test
	void testAddCancionInvalidPlaylist() {
		assertThrows(EntityNotFoundException.class, () -> {
			CancionEntity cancion = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(cancion);
			playlistCancionService.addCancion(0L, cancion.getId());
		});
	}

	@Test
	void testGetCanciones() throws EntityNotFoundException {
		Set<CancionEntity> cancionEntities = playlistCancionService.getCanciones(playlist.getId()); 	

		assertEquals(cancionList.size(), cancionEntities.size());

		for (int i = 0; i < cancionList.size(); i++) {
			assertTrue(cancionEntities.contains(cancionList.iterator().next()));
		}
	}

	@Test
	void testGetCancionWithInvalidPlaylist() {
		assertThrows(EntityNotFoundException.class, () -> {
			playlistCancionService.getCanciones(0L);
		});
	}

	@Test
	void testGetCancion() throws EntityNotFoundException, IllegalOperationException {
		CancionEntity cancionEntity = cancionList.iterator().next();
		CancionEntity cancion = playlistCancionService.getCancion(playlist.getId(), cancionEntity.getId());
		assertNotNull(cancion);

		assertEquals(cancionEntity.getId(), cancion.getId());
		assertEquals(cancionEntity.getTitulo(), cancion.getTitulo());
		assertEquals(cancionEntity.getDuracion(), cancion.getDuracion());
	}

	@Test
	void testGetInvalidCancion() {
		assertThrows(EntityNotFoundException.class, () -> {
			playlistCancionService.getCancion(playlist.getId(), 0L);
		});
	}

	@Test
	void testGetCancionInvalidPlaylist() {
		assertThrows(EntityNotFoundException.class, () -> {
			CancionEntity cancionEntity = cancionList.iterator().next();
			playlistCancionService.getCancion(0L, cancionEntity.getId());
		});
	}

	@Test
	void testGetNotAssociatedCancion() {
		assertThrows(IllegalOperationException.class, () -> {
			PlaylistEntity newPlaylist = factory.manufacturePojo(PlaylistEntity.class);
			entityManager.persist(newPlaylist);
			CancionEntity cancion = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(cancion);
			playlistCancionService.getCancion(newPlaylist.getId(), cancion.getId());
		});
	}

	@Test
	void testReplaceCanciones() throws EntityNotFoundException {
		Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
		for (int i = 0; i < 3; i++) {
			CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(entity);
			playlist.getCanciones().add(entity);
			nuevaLista.add(entity);
		}
		playlistCancionService.replaceCanciones(playlist.getId(), nuevaLista);

		Set<CancionEntity> cancionEntities = playlistCancionService.getCanciones(playlist.getId());
		for (CancionEntity aNuevaLista : nuevaLista) {
			assertTrue(cancionEntities.contains(aNuevaLista));
		}
	}

	@Test
	void testReplaceCanciones2() throws EntityNotFoundException {
		Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
		for (int i = 0; i < 3; i++) {
			CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		playlistCancionService.replaceCanciones(playlist.getId(), nuevaLista);

		Set<CancionEntity> cancionEntities = playlistCancionService.getCanciones(playlist.getId());
		for (CancionEntity aNuevaLista : nuevaLista) {
			assertTrue(cancionEntities.contains(aNuevaLista));
		}
	}

	@Test
	void testReplaceCancionesInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, () -> {
			Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
			for (int i = 0; i < 3; i++) {
				CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
				
				nuevaLista.add(entity);
			}
			playlistCancionService.replaceCanciones(0L, nuevaLista);
		});
	}

	@Test
	void testReplaceInvalidCanciones() {
		assertThrows(EntityNotFoundException.class, () -> {
			Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
			CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			playlistCancionService.replaceCanciones(0L, nuevaLista);
		});
	}

	@Test
	void testReplaceCancionesInvalidCancion() {
		assertThrows(EntityNotFoundException.class, () -> {
			Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
			for (int i = 0; i < 3; i++) {
				CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
				
				nuevaLista.add(entity);
			}
			playlistCancionService.replaceCanciones(0L, nuevaLista);
		});
	}

	@Test
	void testRemoveCancion() throws EntityNotFoundException {
		for (CancionEntity cancion : cancionList) {
			playlistCancionService.removeCancion(playlist.getId(), cancion.getId());
		}
		assertTrue(playlistCancionService.getCanciones(playlist.getId()).isEmpty());
	}

	@Test
	void removeInvalidCancion() {
		assertThrows(EntityNotFoundException.class, () -> {
			playlistCancionService.removeCancion(playlist.getId(), 0L);
		});
	}


	@Test
	void testRemoveCancionInvalidPlaylist() {
		assertThrows(EntityNotFoundException.class, () -> {
			playlistCancionService.removeCancion(0L, cancionList.iterator().next().getId());
		});
	}
}



