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

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.musica.services.CancionService;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Tests for Cancion.
 *
 * @author mar-cas3
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(CancionService.class)
class CancionServiceTest {

	@Autowired
	private CancionService cancionService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<CancionEntity> cancionList = new ArrayList<>();

	private Set<AlbumEntity> albumList = new LinkedHashSet<>();

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
		entityManager.getEntityManager().createQuery("delete from CancionEntity").executeUpdate();
	}

	/**
	 * Insert initial data for test.
	 */
	private void insertData() {

		// Datos de album
		for (int i = 0; i < 3; i++) {
			AlbumEntity albumEntity = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(albumEntity);
			albumList.add(albumEntity);

		}

		for (int i = 0; i < 3; i++) {
			CancionEntity cancionEntity = factory.manufacturePojo(CancionEntity.class);
			System.out.println(cancionEntity.getId() + "kfkfkf");
			entityManager.persist(cancionEntity);
			cancionEntity.setAlbumes(albumList);
			cancionList.add(cancionEntity);
		}
	}

	/**
	 * Test to create Cancion.
	 * 
	 * @throws IllegalOperationException
	 */
	@Test
	void testCreateCancion() throws IllegalOperationException {
		CancionEntity newEntity = factory.manufacturePojo(CancionEntity.class);
		newEntity.setAlbumes(albumList);

		CancionEntity result = cancionService.createCancion(newEntity);
		assertNotNull(result); // Valida que no haya sido null

		CancionEntity entity = entityManager.find(CancionEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getTitulo(), entity.getTitulo());
		assertEquals(newEntity.getDuracion(), entity.getDuracion());

	}

	/**
	 * Test to list Cancion.
	 */
	@Test
	void testGetCanciones() {
		List<CancionEntity> list = cancionService.getCanciones();
		assertEquals(list.size(), cancionList.size());

		for (CancionEntity entity : list) {
			boolean found = false;
			for (CancionEntity storedEntity : cancionList) {
				if (entity.getId().equals(storedEntity.getId()))
					found = true;
			}
			assertTrue(found);
		}

	}

	/**
	 * Test to consult Cancion.
	 * 
	 */
	@Test
	void testGetCancion() throws EntityNotFoundException {
		CancionEntity entity = cancionList.get(0);
		CancionEntity resultEntity = cancionService.getCancion(entity.getId());
		assertNotNull(resultEntity);

		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getTitulo(), resultEntity.getTitulo());
		assertEquals(entity.getDuracion(), resultEntity.getDuracion());

	}

	/**
	 * Test to consult invalid Cancion.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */
	@Test
	void testGetInvalidCancion() {
		assertThrows(EntityNotFoundException.class, () -> {
			cancionService.getCancion(0L);
		});
	}

	/**
	 * Test to update Cancion.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */
	@Test
	void testUpdateCancion() throws EntityNotFoundException, IllegalOperationException {
		CancionEntity entity = cancionList.get(0);
		CancionEntity pojoEntity = factory.manufacturePojo(CancionEntity.class);
		pojoEntity.setId(entity.getId());
		pojoEntity.setAlbumes(albumList);
		cancionService.updateCancion(entity.getId(), pojoEntity);
		CancionEntity resultEntity = entityManager.find(CancionEntity.class, entity.getId());

		assertEquals(pojoEntity.getId(), resultEntity.getId());
		assertEquals(pojoEntity.getTitulo(), resultEntity.getTitulo());
		assertEquals(pojoEntity.getDuracion(), resultEntity.getDuracion());

	}

	/**
	 * Test to update invalid Cancion.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */

	@Test
	void testUpdateInvalidCancion() throws EntityNotFoundException, IllegalOperationException {

		assertThrows(EntityNotFoundException.class, () -> {
			CancionEntity pojoEntity = factory.manufacturePojo(CancionEntity.class);
			pojoEntity.setId(0L);
			cancionService.updateCancion(0l, pojoEntity);
		});

	}

	/**
	 * Test to delete Cancion.
	 * 
	 * @throws co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException
	 */
	@Test
	void testDeleteCancion() throws EntityNotFoundException, IllegalOperationException {

		CancionEntity entity = cancionList.get(1);
		cancionService.deleteCancion(entity.getId());

		CancionEntity deletedEntity = entityManager.find(CancionEntity.class, entity.getId());

		assertNull(deletedEntity);

	}

	@Test
	void testDeleteInvalidCancion() {

		assertThrows(EntityNotFoundException.class, ()-> {
			cancionService.deleteCancion(0L);
		});

	}

}