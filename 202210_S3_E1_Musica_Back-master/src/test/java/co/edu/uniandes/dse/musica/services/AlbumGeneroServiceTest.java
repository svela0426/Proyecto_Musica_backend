package co.edu.uniandes.dse.musica.services;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
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
import co.edu.uniandes.dse.musica.entities.GeneroEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de la relacion Album - Genero
 *
 * @genero ISIS2603
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(AlbumGeneroService.class)
class AlbumGeneroServiceTest {
	
	@Autowired
	private AlbumGeneroService albumGeneroService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private AlbumEntity album = new AlbumEntity();
	private Set<GeneroEntity> generoList = new LinkedHashSet<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from GeneroEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from AlbumEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

		album = factory.manufacturePojo(AlbumEntity.class);
		entityManager.persist(album);

		for (int i = 0; i < 3; i++) {
			GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(entity);
			entity.getAlbumes().add(album);
			generoList.add(entity);
			album.getGeneros().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un autor a un libro.
	 *
	 */
	@Test
	void testAddGenero() throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity newAlbum = factory.manufacturePojo(AlbumEntity.class);
		entityManager.persist(newAlbum);
		
		GeneroEntity genero = factory.manufacturePojo(GeneroEntity.class);
		entityManager.persist(genero);
		
		albumGeneroService.addGenero(newAlbum.getId(), genero.getId());
		
		GeneroEntity lastGenero = albumGeneroService.getGenero(newAlbum.getId(), genero.getId());
		assertEquals(genero.getId(), lastGenero.getId());
		assertEquals(genero.getAlbumes(), lastGenero.getAlbumes());
		assertEquals(genero.getNombre(), lastGenero.getNombre());
	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidGenero() {
		assertThrows(EntityNotFoundException.class, ()->{
			AlbumEntity newAlbum = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(newAlbum);
			albumGeneroService.addGenero(newAlbum.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor a un libro que no existe.
	 *
	 */
	@Test
	void testAddGeneroInvalidAlbum() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			GeneroEntity genero = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(genero);
			albumGeneroService.addGenero(0L, genero.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetGeneros() throws EntityNotFoundException {
		Set<GeneroEntity> generoEntities = albumGeneroService.getGeneros(album.getId());

		assertEquals(generoList.size(), generoEntities.size());

		for (int i = 0; i < generoList.size(); i++) {
			assertTrue(generoEntities.equals(generoList));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetGenerosInvalidAlbum(){
		assertThrows(EntityNotFoundException.class, ()->{
			albumGeneroService.getGeneros(0L);
		});
	}

	/**
	 * Prueba para consultar un autor de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetGenero() throws EntityNotFoundException, IllegalOperationException {
		Iterator<GeneroEntity> i= generoList.iterator();  
		GeneroEntity generoEntity = i.next();
		GeneroEntity genero = albumGeneroService.getGenero(album.getId(), generoEntity.getId());
		assertNotNull(genero);
		assertEquals(genero.getId(), generoEntity.getId());
		assertEquals(genero.getAlbumes(), generoEntity.getAlbumes());
		assertEquals(genero.getNombre(), generoEntity.getNombre());
	}
	
	/**
	 * Prueba para consultar un autor que no existe de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidGenero()  {
		assertThrows(EntityNotFoundException.class, ()->{
			albumGeneroService.getGenero(album.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un autor de un libro que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetGeneroInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, ()->{
			Iterator<GeneroEntity> i= generoList.iterator();  
			GeneroEntity generoEntity = i.next();
			albumGeneroService.getGenero(0L, generoEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un autor no asociado a un libro.
	 *
	 */
	@Test
	void testGetNotAssociatedGenero() {
		assertThrows(IllegalOperationException.class, ()->{
			AlbumEntity newAlbum = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(newAlbum);
			GeneroEntity genero = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(genero);
			albumGeneroService.getGenero(newAlbum.getId(), genero.getId());
		});
	}

	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceGeneros() throws EntityNotFoundException {
		List<GeneroEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(entity);
			album.getGeneros().add(entity);
			nuevaLista.add(entity);
		}
		albumGeneroService.replaceGeneros(album.getId(), nuevaLista);
		
		Set<GeneroEntity> generoEntities = albumGeneroService.getGeneros(album.getId());
		for (GeneroEntity aNuevaLista : nuevaLista) {
			assertTrue(generoEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceGeneros2() throws EntityNotFoundException {
		List<GeneroEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		albumGeneroService.replaceGeneros(album.getId(), nuevaLista);
		
		Set<GeneroEntity> generoEntities = albumGeneroService.getGeneros(album.getId());
		for (GeneroEntity aNuevaLista : nuevaLista) {
			assertTrue(generoEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los autores de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceGenerosInvalidAlbum(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<GeneroEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
				entity.getAlbumes().add(album);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			albumGeneroService.replaceGeneros(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los autores que no existen de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidGeneros() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<GeneroEntity> nuevaLista = new ArrayList<>();
			GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			albumGeneroService.replaceGeneros(album.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un autor de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceGenerosInvalidGenero(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<GeneroEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
				entity.getAlbumes().add(album);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			albumGeneroService.replaceGeneros(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro.
	 * @throws IllegalOperationException 
	 *
	 */
	@Test
	void testRemoveGenero() throws EntityNotFoundException, IllegalOperationException {
		int sizeInit = albumGeneroService.getGeneros(album.getId()).size();
		
		Iterator<GeneroEntity> i= generoList.iterator();  
		GeneroEntity generoEntity = i.next();
		
		
		albumGeneroService.removeGenero(album.getId(), generoEntity.getId());
		
		assertEquals(sizeInit-1, albumGeneroService.getGeneros(album.getId()).size());
	}
	
	@Test
	void testRemoveAllGeneros() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(IllegalOperationException.class, ()->{
			for (GeneroEntity genero : generoList) {
				albumGeneroService.removeGenero(album.getId(), genero.getId());
			}
		});
	}
	
	
	
	/**
	 * Prueba desasociar un autor que no existe con un libro.
	 *
	 */
	@Test
	void testRemoveInvalidGenero(){
		assertThrows(EntityNotFoundException.class, ()->{
			albumGeneroService.removeGenero(album.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemoveGeneroInvalidAlbum(){
		assertThrows(EntityNotFoundException.class, ()->{
			Iterator<GeneroEntity> i= generoList.iterator();  
			GeneroEntity generoEntity = i.next();
			albumGeneroService.removeGenero(0L, generoEntity.getId());
		});
	}

}

