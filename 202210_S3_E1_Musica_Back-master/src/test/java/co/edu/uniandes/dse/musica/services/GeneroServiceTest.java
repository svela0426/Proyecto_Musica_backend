package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.GeneroEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas para genero
 * 
 * @author juancamilobonet2
 */

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(GeneroService.class)
class GeneroServiceTest {
	
	@Autowired
	private GeneroService generoService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();
	
	private List<GeneroEntity> generoList = new ArrayList<>();
	
	private List<AlbumEntity> albumList = new ArrayList<>();
	
	
	/**
	 * Configuración inicial de la prueba.
	 */
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from GeneroEntity").executeUpdate();;
		entityManager.getEntityManager().createQuery("delete from AlbumEntity").executeUpdate();
	}
	
	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
		
		for (int i = 0; i < 3; i++) {
			// crea 3 generos para pruebas
			GeneroEntity generoEntity = factory.manufacturePojo(GeneroEntity.class);
			
			for (int j = 0; j < 3; j++) {
				//Crea los albumes que pertenecen a un genero
				AlbumEntity albumEntity = factory.manufacturePojo(AlbumEntity.class);
				entityManager.persist(albumEntity);
				albumList.add(albumEntity);
			}
			
			generoEntity.setAlbumes(albumList);
			entityManager.persist(generoEntity);
			generoList.add(generoEntity);
		}
	}
	
	
	
	@Test
	void testCreateGenero() {
		//prueba la creacion de un nuevo genero
		GeneroEntity newGenero = factory.manufacturePojo(GeneroEntity.class);
		newGenero.setAlbumes(albumList);
		GeneroEntity result = generoService.createGenero(newGenero);
		assertNotNull(result);
		GeneroEntity entity = entityManager.find(GeneroEntity.class, result.getId());
		
		assertEquals(newGenero.getId(), entity.getId());
		assertEquals(newGenero.getNombre(), entity.getNombre());
		assertEquals(newGenero.getAlbumes(), entity.getAlbumes());
	}
	
	@Test
	void testGetTodosGeneros() {
		//Prueba la funcion de obtener todos los generos
		List<GeneroEntity> list = generoService.getGeneros();
		assertEquals(generoList.size(), list.size());
		for (GeneroEntity entity : list) {
			boolean found = false;
			for (GeneroEntity storedEntity : generoList) {
				if (entity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}
	
	@Test
	void testGetGenero() throws EntityNotFoundException {
		// prueba la funcion de obtener un genero por id
		GeneroEntity entity = generoList.get(0);
		GeneroEntity resultEntity = generoService.getGenero(entity.getId());
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getNombre(), resultEntity.getNombre());
		assertEquals(resultEntity.getAlbumes(), entity.getAlbumes());
		
	}
	
	@Test
	void testGetGeneroInvalido() {
		//prueba que tira error al buscar una entidad inexistente
		assertThrows(EntityNotFoundException.class,()->{
			generoService.getGenero(0L);
		});
	}
	
	@Test
	void testUpdateGenero() throws EntityNotFoundException {
		//prueba la funcion de actualizar un genero
		GeneroEntity entity = generoList.get(0);
		GeneroEntity pojoEntity = factory.manufacturePojo(GeneroEntity.class);
		pojoEntity.setId(entity.getId());
		generoService.updateGenero(entity.getId(), pojoEntity);
		
		GeneroEntity resp = entityManager.find(GeneroEntity.class, entity.getId());
		assertEquals(pojoEntity.getId(), resp.getId());
		assertEquals(pojoEntity.getNombre(), resp.getNombre());
		assertEquals(pojoEntity.getAlbumes(), resp.getAlbumes());
	}
	
	@Test
	void testUpdateGeneroInvalido() {
		assertThrows(EntityNotFoundException.class, () -> {
			GeneroEntity pojoEntity = factory.manufacturePojo(GeneroEntity.class);
			pojoEntity.setId(0L);
			generoService.updateGenero(0L, pojoEntity);
		});
	}
	
	@Test
	void testBorrarGenero() throws EntityNotFoundException {
		GeneroEntity entity = generoList.get(1);
		generoService.deleteGenero(entity.getId());
		GeneroEntity deleted = entityManager.find(GeneroEntity.class, entity.getId());
		assertNull(deleted);
	}
	
	@Test
	void testBorrarGeneroInvalido() {
		assertThrows(EntityNotFoundException.class, ()->{
			generoService.deleteGenero(0L);
		});
	}
	
	

}
