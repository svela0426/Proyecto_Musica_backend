package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.entities.GeneroEntity;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Tests for Album.
 *
 * @author mar-cas3
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(AlbumService.class)
class AlbumServiceTest {

	@Autowired
	private AlbumService albumService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private Set<AlbumEntity> albumList = new LinkedHashSet<>();

	private Set<CancionEntity> cancionList = new LinkedHashSet<>();

	private Set<CreadorEntity> artistasList = new LinkedHashSet<>();

	private Set<GeneroEntity> generoList  = new LinkedHashSet<>();

	/**
	 * Initial test configuration.
	 */
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	/**
	 * Clean tables in the test.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from AlbumEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from CancionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from CreadorEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from GeneroEntity").executeUpdate();
	}

	/**
	 * Insert initial data for test.
	 */
	private void insertData() {

		// Datos de canciones de album
		for (int i = 0; i < 3; i++) {
			CancionEntity cancionEntity = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(cancionEntity);
			cancionList.add(cancionEntity);
		}

		// Datos de creadores de album
		for (int i = 0; i < 3; i++) {
			CreadorEntity creadorEntity = factory.manufacturePojo(CreadorEntity.class);
			entityManager.persist(creadorEntity);
			artistasList.add(creadorEntity);
		}

		// Datos de generos de album
		for (int i = 0; i < 3; i++) {
			GeneroEntity generoEntity = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(generoEntity);
			generoList.add(generoEntity);
		}

		// Datos de album
		for (int i = 0; i < 3; i++) {
			AlbumEntity albumEntity = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(albumEntity);
			albumList.add(albumEntity);

		}

		// When cycling through LinkedHashSet using an iterator,
		// the elements will be returned to the order in which they were inserted.

		generoList.iterator().next().setAlbumes(new ArrayList<AlbumEntity>(albumList));
		albumList.iterator().next().getGeneros().add(generoList.iterator().next());

		cancionList.iterator().next().setAlbumes(albumList);
		albumList.iterator().next().getCanciones().add(cancionList.iterator().next());

		artistasList.iterator().next().setAlbumes(albumList);
		albumList.iterator().next().getArtistas().add(artistasList.iterator().next());
	}

	/**
	 * Test to create Album.
	 * 
	 * @throws IllegalOperationException
	 */
	@Test
	void testCreateAlbum() throws IllegalOperationException {
		AlbumEntity newEntity = factory.manufacturePojo(AlbumEntity.class);

		newEntity.setCanciones(cancionList);
		newEntity.setArtistas(artistasList);
		AlbumEntity result = albumService.createAlbum(newEntity);
		assertNotNull(result);

		AlbumEntity entity = entityManager.find(AlbumEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getTitulo(), entity.getTitulo());
		assertEquals(newEntity.getImagen(), entity.getImagen());

		// Validar que las canciones y los artistas sean los mismos
		assertTrue(newEntity.getCanciones().equals(entity.getCanciones()));
		assertTrue(newEntity.getArtistas().equals(entity.getArtistas()));
		assertTrue(newEntity.getGeneros().equals(entity.getGeneros())); 
	}

	@Test
	void testCreateInvalidAlbum() throws IllegalOperationException {
		assertThrows(IllegalOperationException.class, () -> {
            AlbumEntity newEntity = factory.manufacturePojo(AlbumEntity.class);
			newEntity.setTitulo("");
			albumService.createAlbum(newEntity);
        });
	}

	/**
	 * Test to list Album.
	 */
	@Test
	void testGetAlbums() {
		List<AlbumEntity> list = albumService.getAlbums();
		assertEquals(list.size(), albumList.size());

		for (AlbumEntity entity : list) {
			boolean found = false;
			for (AlbumEntity storedEntity : albumList) {
				if (entity.getId().equals(storedEntity.getId()))
					found = true;
			}
			assertTrue(found);
		}

	}

	/**
	 * Test to consult Album.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */
	@Test
	void testGetAlbum() throws EntityNotFoundException {
		AlbumEntity entity = albumList.iterator().next();
		AlbumEntity resultEntity = albumService.getAlbum(entity.getId());
		assertNotNull(resultEntity);

		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getTitulo(), resultEntity.getTitulo());
		assertEquals(entity.getImagen(), resultEntity.getImagen());

		// assertEquals(entity.getCanciones(), resultEntity.getCanciones());
	}

	/**
	 * Test to consult invalid Album.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */
	@Test
	void testGetInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, () -> {
			albumService.getAlbum(0L);
		});
	}

	/**
	 * Test to update Album.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */
	@Test
	void testUpdateAlbum() throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity entity = albumList.iterator().next();
		AlbumEntity pojoEntity = factory.manufacturePojo(AlbumEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setArtistas(artistasList);
		pojoEntity.setCanciones(cancionList);
		pojoEntity.setGeneros(generoList);
		albumService.updateAlbum(entity.getId(), pojoEntity);
		AlbumEntity resultEntity = entityManager.find(AlbumEntity.class, entity.getId());

		assertEquals(pojoEntity.getId(), resultEntity.getId());
		assertEquals(pojoEntity.getTitulo(), resultEntity.getTitulo());
		assertEquals(pojoEntity.getImagen(), resultEntity.getImagen());

		// Validar que las canciones y los artistas sean los mismos
		assertTrue(pojoEntity.getCanciones().equals(resultEntity.getCanciones()));
		assertTrue(pojoEntity.getArtistas().equals(resultEntity.getArtistas()));

	}

	/**
	 * Test to update invalid Album.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */
	@Test
	void testUpdateInvalidAlbum() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, () -> {
			AlbumEntity pojoEntity = factory.manufacturePojo(AlbumEntity.class);
			pojoEntity.setId(0L);
			albumService.updateAlbum(0l, pojoEntity);
		});

	}

	@Test
	void testUpdateAlbumEmptyTitle() throws IllegalOperationException {
		assertThrows(IllegalOperationException.class, () -> {
            AlbumEntity newEntity = factory.manufacturePojo(AlbumEntity.class);
			newEntity.setTitulo("");
			albumService.updateAlbum(albumList.iterator().next().getId(), newEntity); 
        });
	}

	/**
	 * Test to delete Album.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */
	@Test
	void testDeleteAlbum() throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity entity = new ArrayList <AlbumEntity>(albumList).get(1);
		albumService.deleteAlbum(entity.getId());
		AlbumEntity deletedEntity = entityManager.find(AlbumEntity.class, entity.getId());
		assertNull(deletedEntity);

	}

	@Test
	void testDeleteAlbumWithSongs() {
		assertThrows(IllegalOperationException.class, () -> {
            AlbumEntity entity = albumList.iterator().next();
			entity.setArtistas(new LinkedHashSet<>());
			entity.setGeneros(new LinkedHashSet<>());
			albumService.deleteAlbum(entity.getId()); 
        });
	}

	@Test
	void testDeleteAlbumWithArtists() {
		assertThrows(IllegalOperationException.class, () -> {
            AlbumEntity entity = albumList.iterator().next();
			entity.setCanciones(new LinkedHashSet<>());
			entity.setGeneros(new LinkedHashSet<>());
			albumService.deleteAlbum(entity.getId()); 
        });
	}

	@Test
	void testDeleteAlbumWithGenres() {
		assertThrows(IllegalOperationException.class, () -> {
            AlbumEntity entity = albumList.iterator().next();
			entity.setArtistas(new LinkedHashSet<>());
			entity.setCanciones(new LinkedHashSet<>());
			albumService.deleteAlbum(entity.getId()); 
        });
	}

}