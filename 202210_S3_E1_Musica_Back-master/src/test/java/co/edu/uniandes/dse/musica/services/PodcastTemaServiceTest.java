
package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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


import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.entities.TemaEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de la relacion Podcast - Tema
 *
 * @podcast ISIS2603
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PodcastTemaService.class)
class PodcastTemaServiceTest {
	
	@Autowired
	private PodcastTemaService podcastTemaService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PodcastEntity podcast = new PodcastEntity();
	private List<TemaEntity> temaList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PodcastEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from TemaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

		podcast = factory.manufacturePojo(PodcastEntity.class);
		entityManager.persist(podcast);

		for (int i = 0; i < 3; i++) {
			TemaEntity entity = factory.manufacturePojo(TemaEntity.class);
			entityManager.persist(entity);
			entity.getPodcasts().add(podcast);
			temaList.add(entity);
			podcast.getTemas().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un autor a un libro.
	 *
	 */
	@Test
	void testAddTema() throws EntityNotFoundException, IllegalOperationException {
		
		PodcastEntity newPodcast = factory.manufacturePojo(PodcastEntity.class);
		entityManager.persist(newPodcast);
		
		TemaEntity tema = factory.manufacturePojo(TemaEntity.class);
		entityManager.persist(tema);
		
		podcastTemaService.addTema(newPodcast.getId(), tema.getId());
		
		TemaEntity lastTema = podcastTemaService.getTema(newPodcast.getId(), tema.getId());
		
		
		assertEquals(tema.getId(), lastTema.getId());
		assertEquals(tema.getNombre(), lastTema.getNombre());
		assertEquals(tema.getPodcasts(), lastTema.getPodcasts());

	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidTema() {
		assertThrows(EntityNotFoundException.class, () -> {
			PodcastEntity newPodcast = factory.manufacturePojo(PodcastEntity.class);
			entityManager.persist(newPodcast);
			podcastTemaService.addTema(newPodcast.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor a un libro que no existe.
	 *
	 */
	@Test
	void testAddTemaInvalidPodcast() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			TemaEntity tema = factory.manufacturePojo(TemaEntity.class);
			entityManager.persist(tema);
			podcastTemaService.addTema(0L, tema.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetTemas() throws EntityNotFoundException {
		Set<TemaEntity> temaEntities = podcastTemaService.getTemas(podcast.getId());

		assertEquals(temaList.size(), temaEntities.size());

		for (int i = 0; i < temaList.size(); i++) {
			assertTrue(temaEntities.contains(temaList.get(i)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetTemasInvalidPodcast(){
		assertThrows(EntityNotFoundException.class, ()->{
			podcastTemaService.getTemas(0L);
		});
	}

	/**
	 * Prueba para consultar un autor de un libro.
	 * @throws IllegalOperationException
	 * @throws EntityNotFoundException
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetTema() throws EntityNotFoundException, IllegalOperationException {
		
		TemaEntity temaEntity = temaList.get(0);
		TemaEntity tema = podcastTemaService.getTema(podcast.getId(), temaEntity.getId());
		
		assertNotNull(tema);

		assertEquals(temaEntity.getId(), tema.getId());
		assertEquals(temaEntity.getNombre(), tema.getNombre());
	}
	
	/**
	 * Prueba para consultar un autor que no existe de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPodcastsInvalidTema()  {
		assertThrows(EntityNotFoundException.class, ()->{
			podcastTemaService.getTema(podcast.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un autor de un libro que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetTemaInvalidPodcast() {
		assertThrows(EntityNotFoundException.class, ()->{
			TemaEntity temaEntity = temaList.get(0);
			podcastTemaService.getTema(0L, temaEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un autor no asociado a un libro.
	 *
	 */
	@Test
	void testGetNotAssociatedTema() {
		assertThrows(IllegalOperationException.class, ()->{
			PodcastEntity newPodcast = factory.manufacturePojo(PodcastEntity.class);
			entityManager.persist(newPodcast);

			TemaEntity temaEntity = factory.manufacturePojo(TemaEntity.class);
			entityManager.persist(temaEntity);
			podcastTemaService.getTema(newPodcast.getId(), temaEntity.getId());
		});
	}

	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceTemas() throws EntityNotFoundException {
		List<TemaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			TemaEntity entity = factory.manufacturePojo(TemaEntity.class);
			entityManager.persist(entity);
			podcast.getTemas().add(entity);
			nuevaLista.add(entity);
		}

		podcastTemaService.replaceTemas(podcast.getId(), nuevaLista);
		
		Set<TemaEntity> temaEntities = podcastTemaService.getTemas(podcast.getId());
		for (TemaEntity aNuevaLista : nuevaLista) {
			assertTrue(temaEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los autores de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceTemaInvalidPodcast(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<TemaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			TemaEntity entity = factory.manufacturePojo(TemaEntity.class);
			entityManager.persist(entity);
			podcast.getTemas().add(entity);
			nuevaLista.add(entity);
		}
			podcastTemaService.replaceTemas(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los autores que no existen de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidTemas() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<TemaEntity> nuevaLista = new ArrayList<>();
			TemaEntity entity = factory.manufacturePojo(TemaEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			podcastTemaService.replaceTemas(podcast.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un autor de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceTemaInvalidTema(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<TemaEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				TemaEntity entity = factory.manufacturePojo(TemaEntity.class);
				entity.getPodcasts().add(podcast);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			podcastTemaService.replaceTemas(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro.
	 *
	 */
	@Test
	void testRemoveTema() throws EntityNotFoundException {
		for (TemaEntity tema : temaList) {
			podcastTemaService.removeTema(podcast.getId(), tema.getId());
		}
		assertTrue(podcastTemaService.getTemas(podcast.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un autor que no existe con un libro.
	 *
	 */
	@Test
	void testRemoveInvalidTema(){
		assertThrows(EntityNotFoundException.class, ()->{
			podcastTemaService.removeTema(podcast.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemovePodcastInvalidPodcast(){
		assertThrows(EntityNotFoundException.class, ()->{
			podcastTemaService.removeTema(0L, temaList.get(0).getId());
		});
	}

}