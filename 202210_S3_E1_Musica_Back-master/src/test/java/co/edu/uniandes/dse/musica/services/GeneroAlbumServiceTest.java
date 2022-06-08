package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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
 * Pruebas de logica de la relacion Genero - Album
 *
 * @album ISIS2603
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(GeneroAlbumService.class)
class GeneroAlbumServiceTest {
	
	@Autowired
	private GeneroAlbumService generoAlbumService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private GeneroEntity genero = new GeneroEntity();
	private List<AlbumEntity> albumList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from AlbumEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from GeneroEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

		genero = factory.manufacturePojo(GeneroEntity.class);
		entityManager.persist(genero);

		for (int i = 0; i < 3; i++) {
			AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(entity);
			entity.getGeneros().add(genero);
			albumList.add(entity);
			genero.getAlbumes().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un autor a un libro.
	 *
	 */
	@Test
	void testAddAlbum() throws EntityNotFoundException, IllegalOperationException {
		GeneroEntity newGenero = factory.manufacturePojo(GeneroEntity.class);
		entityManager.persist(newGenero);
		
		AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
		entityManager.persist(album);
		
		generoAlbumService.addAlbum(newGenero.getId(), album.getId());
		
		AlbumEntity lastAlbum = generoAlbumService.getAlbum(newGenero.getId(), album.getId());
		assertEquals(album.getId(), lastAlbum.getId());
		assertEquals(album.getTitulo(), lastAlbum.getTitulo());
		assertEquals(album.getImagen(), lastAlbum.getImagen());
		assertEquals(album.getArtistas(), lastAlbum.getArtistas());
		assertEquals(album.getGeneros(), lastAlbum.getGeneros());
		assertEquals(album.getCanciones(), lastAlbum.getCanciones());
	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, ()->{
			GeneroEntity newGenero = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(newGenero);
			generoAlbumService.addAlbum(newGenero.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor a un libro que no existe.
	 *
	 */
	@Test
	void testAddAlbumInvalidGenero() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(album);
			generoAlbumService.addAlbum(0L, album.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetAlbums() throws EntityNotFoundException {
		List<AlbumEntity> albumEntities = generoAlbumService.getAlbums(genero.getId());

		assertEquals(albumList.size(), albumEntities.size());

		for (int i = 0; i < albumList.size(); i++) {
			assertTrue(albumEntities.contains(albumList.get(i)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetAlbumsInvalidGenero(){
		assertThrows(EntityNotFoundException.class, ()->{
			generoAlbumService.getAlbums(0L);
		});
	}

	/**
	 * Prueba para consultar un autor de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetAlbum() throws EntityNotFoundException, IllegalOperationException {
		AlbumEntity albumEntity = albumList.get(0);
		AlbumEntity album = generoAlbumService.getAlbum(genero.getId(), albumEntity.getId());
		assertNotNull(album);

		assertEquals(albumEntity.getId(), album.getId());
		assertEquals(albumEntity.getTitulo(), album.getTitulo());
		assertEquals(albumEntity.getImagen(), album.getImagen());
		assertEquals(albumEntity.getArtistas(), album.getArtistas());
		assertEquals(albumEntity.getGeneros(), album.getGeneros());
		assertEquals(albumEntity.getCanciones(), album.getCanciones());
	}
	
	/**
	 * Prueba para consultar un autor que no existe de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidAlbum()  {
		assertThrows(EntityNotFoundException.class, ()->{
			generoAlbumService.getAlbum(genero.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un autor de un libro que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetAlbumInvalidGenero() {
		assertThrows(EntityNotFoundException.class, ()->{
			AlbumEntity albumEntity = albumList.get(0);
			generoAlbumService.getAlbum(0L, albumEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un autor no asociado a un libro.
	 *
	 */
	@Test
	void testGetNotAssociatedAlbum() {
		assertThrows(IllegalOperationException.class, ()->{
			GeneroEntity newGenero = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(newGenero);
			AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(album);
			generoAlbumService.getAlbum(newGenero.getId(), album.getId());
		});
	}

	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceAlbums() throws EntityNotFoundException {
		List<AlbumEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(entity);
			genero.getAlbumes().add(entity);
			nuevaLista.add(entity);
		}
		generoAlbumService.replaceAlbums(genero.getId(), nuevaLista);
		
		List<AlbumEntity> albumEntities = generoAlbumService.getAlbums(genero.getId());
		for (AlbumEntity aNuevaLista : nuevaLista) {
			assertTrue(albumEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceAlbums2() throws EntityNotFoundException {
		List<AlbumEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		generoAlbumService.replaceAlbums(genero.getId(), nuevaLista);
		
		List<AlbumEntity> albumEntities = generoAlbumService.getAlbums(genero.getId());
		for (AlbumEntity aNuevaLista : nuevaLista) {
			assertTrue(albumEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los autores de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceAlbumsInvalidGenero(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<AlbumEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
				entity.getGeneros().add(genero);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			generoAlbumService.replaceAlbums(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los autores que no existen de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidAlbums() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<AlbumEntity> nuevaLista = new ArrayList<>();
			AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			generoAlbumService.replaceAlbums(genero.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un autor de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceAlbumsInvalidAlbum(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<AlbumEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
				entity.getGeneros().add(genero);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			generoAlbumService.replaceAlbums(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro.
	 *
	 */
	@Test
	void testRemoveAlbum() throws EntityNotFoundException {
		for (AlbumEntity album : albumList) {
			generoAlbumService.removeAlbum(genero.getId(), album.getId());
		}
		assertTrue(generoAlbumService.getAlbums(genero.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un autor que no existe con un libro.
	 *
	 */
	@Test
	void testRemoveInvalidAlbum(){
		assertThrows(EntityNotFoundException.class, ()->{
			generoAlbumService.removeAlbum(genero.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemoveAlbumInvalidGenero(){
		assertThrows(EntityNotFoundException.class, ()->{
			generoAlbumService.removeAlbum(0L, albumList.get(0).getId());
		});
	}

}
