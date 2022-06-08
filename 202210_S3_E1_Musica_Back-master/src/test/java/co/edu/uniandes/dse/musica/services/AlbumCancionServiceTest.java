package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(AlbumCancionService.class)
class AlbumCancionServiceTest {

	@Autowired
	private AlbumCancionService albumCancionService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private AlbumEntity album = new AlbumEntity();
	private Set<CancionEntity> cancionList = new LinkedHashSet<>();

	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from AlbumEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from CancionEntity").executeUpdate();
	}

	private void insertData() {

		album = factory.manufacturePojo(AlbumEntity.class);
		entityManager.persist(album);

		for (int i = 0; i < 3; i++) {
			CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(entity);
			entity.getAlbumes().add(album);
			cancionList.add(entity);
			album.getCanciones().add(entity);
		}
	}

	@Test
	void testAddCancion() throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity newAlbum = factory.manufacturePojo(AlbumEntity.class);
		entityManager.persist(newAlbum);

		CancionEntity cancion = factory.manufacturePojo(CancionEntity.class);
		entityManager.persist(cancion);

		albumCancionService.addCancion(newAlbum.getId(), cancion.getId());

		CancionEntity lastCancion = albumCancionService.getCancion(newAlbum.getId(), cancion.getId());

		assertEquals(cancion.getId(), lastCancion.getId());
		assertEquals(cancion.getTitulo(), lastCancion.getTitulo());
		assertEquals(cancion.getDuracion(), lastCancion.getDuracion());
	}

	@Test
	void testAddCancionInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, () -> {
			CancionEntity cancion = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(cancion);
			albumCancionService.addCancion(0L, cancion.getId());
		});
	}

	@Test
	void testAddExistingCancionTitleToAlbum() {
		assertThrows(IllegalOperationException.class, () -> {
			ArrayList<CancionEntity> listCanciones = new ArrayList<CancionEntity>(cancionList);
			listCanciones.get(0).setTitulo("Cancion"); 
			albumCancionService.addCancion(album.getId(), listCanciones.get(0).getId()); 
            listCanciones.get(1).setTitulo("Cancion");
            albumCancionService.addCancion(album.getId(), listCanciones.get(1).getId()); 
        });
	}

	@Test
	void testGetCanciones() throws EntityNotFoundException {
		Set<CancionEntity> cancionEntities = albumCancionService.getCanciones(album.getId());

		assertEquals(cancionList.size(), cancionEntities.size());

		for (int i = 0; i < cancionList.size(); i++) {
			assertTrue(cancionEntities.contains(cancionList.iterator().next()));
		}
	}

	@Test
	void testGetCancionWithInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, () -> {
			albumCancionService.getCanciones(0L);
		});
	}

	@Test
	void testGetCancion() throws EntityNotFoundException, IllegalOperationException {
		CancionEntity cancionEntity = cancionList.iterator().next();
		CancionEntity cancion = albumCancionService.getCancion(album.getId(), cancionEntity.getId());
		assertNotNull(cancion);

		assertEquals(cancionEntity.getId(), cancion.getId());
		assertEquals(cancionEntity.getTitulo(), cancion.getTitulo());
		assertEquals(cancionEntity.getDuracion(), cancion.getDuracion());
	}

	@Test
	void testGetInvalidCancion() {
		assertThrows(EntityNotFoundException.class, () -> {
			albumCancionService.getCancion(album.getId(), 0L);
		});
	}

	@Test
	void testGetCancionInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, () -> {
			CancionEntity cancionEntity = cancionList.iterator().next();
			albumCancionService.getCancion(0L, cancionEntity.getId());
		});
	}

	@Test
	void testGetNotAssociatedCancion() {
		assertThrows(IllegalOperationException.class, () -> {
			AlbumEntity newAlbum = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(newAlbum);
			CancionEntity cancion = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(cancion);
			albumCancionService.getCancion(newAlbum.getId(), cancion.getId());
		});
	}

	@Test
	void testReplaceCanciones() throws EntityNotFoundException, IllegalOperationException {
		Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
		for (int i = 0; i < 3; i++) {
			CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(entity);
			album.getCanciones().add(entity);
			nuevaLista.add(entity);
		}
		albumCancionService.replaceCanciones(album.getId(), nuevaLista);

		Set<CancionEntity> cancionEntities = albumCancionService.getCanciones(album.getId());
		for (CancionEntity aNuevaLista : nuevaLista) {
			assertTrue(cancionEntities.contains(aNuevaLista));
		}
	}

	@Test
	void testReplaceCanciones2() throws EntityNotFoundException, IllegalOperationException {
		Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
		for (int i = 0; i < 3; i++) {
			CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		albumCancionService.replaceCanciones(album.getId(), nuevaLista);

		Set<CancionEntity> cancionEntities = albumCancionService.getCanciones(album.getId());
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
				entity.getAlbumes().add(album);
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			albumCancionService.replaceCanciones(0L, nuevaLista);
		});
	}

	@Test
	void testReplaceInvalidCanciones() {
		assertThrows(EntityNotFoundException.class, () -> {
			Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
			CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			albumCancionService.replaceCanciones(0L, nuevaLista);
		});
	}

	@Test
	void testReplaceCancionesInvalidCancion() {
		assertThrows(EntityNotFoundException.class, () -> {
			Set<CancionEntity> nuevaLista = new LinkedHashSet<>();
			for (int i = 0; i < 3; i++) {
				CancionEntity entity = factory.manufacturePojo(CancionEntity.class);
				entity.getAlbumes().add(album);
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			albumCancionService.replaceCanciones(0L, nuevaLista);
		});
	}

	@Test
	void testRemoveCancion() throws EntityNotFoundException {
		for (CancionEntity cancion : cancionList) {
			albumCancionService.removeCancion(album.getId(), cancion.getId());
		}
		assertTrue(albumCancionService.getCanciones(album.getId()).isEmpty());
	}

	@Test
	void testRemoveInvalidCancion() {
		assertThrows(EntityNotFoundException.class, () -> {
			albumCancionService.removeCancion(album.getId(), 0L);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemoveCancionInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, () -> {
			albumCancionService.removeCancion(0L, cancionList.iterator().next().getId());
		});
	}
}
