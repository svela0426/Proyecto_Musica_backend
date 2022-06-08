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
@Import(TemaPodcastService.class)
class TemaPodcastServiceTest {
	
	@Autowired
	private TemaPodcastService podcastTemaService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private TemaEntity tema = new TemaEntity();
	private List<PodcastEntity> podcastList = new ArrayList<>();

	
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

		tema = factory.manufacturePojo(TemaEntity.class);
		entityManager.persist(tema);

		for (int i = 0; i < 3; i++) {
			PodcastEntity entity = factory.manufacturePojo(PodcastEntity.class);
			entityManager.persist(entity);
			entity.getTemas().add(tema);
			podcastList.add(entity);
			tema.getPodcasts().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un autor a un libro.
	 *
	 */
	@Test
	void testAddPodcast() throws EntityNotFoundException, IllegalOperationException {
		TemaEntity newTema = factory.manufacturePojo(TemaEntity.class);
		entityManager.persist(newTema);
		
		PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
		entityManager.persist(podcast);
		
		podcastTemaService.addPodcast(newTema.getId(), podcast.getId());
		
		PodcastEntity lastPodcast = podcastTemaService.getPodcast(newTema.getId(), podcast.getId());
		assertEquals(podcast.getId(), lastPodcast.getId());
		assertEquals(podcast.getTitulo(), lastPodcast.getTitulo());
		assertEquals(podcast.getImagen(), lastPodcast.getImagen());
		assertEquals(podcast.getCalificacion(), lastPodcast.getCalificacion());
		assertEquals(podcast.getDescripcion(), lastPodcast.getDescripcion());
		assertEquals(podcast.getPrecio(), lastPodcast.getPrecio());
	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidPodcast() {
		assertThrows(EntityNotFoundException.class, ()->{
			TemaEntity newTema = factory.manufacturePojo(TemaEntity.class);
			entityManager.persist(newTema);
			podcastTemaService.addPodcast(newTema.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor a un libro que no existe.
	 *
	 */
	@Test
	void testAddPodcastInvalidTema() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
			entityManager.persist(podcast);
			podcastTemaService.addPodcast(0L, podcast.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetPodcasts() throws EntityNotFoundException {
		List<PodcastEntity> podcastEntities = podcastTemaService.getPodcasts(tema.getId());

		assertEquals(podcastList.size(), podcastEntities.size());

		for (int i = 0; i < podcastList.size(); i++) {
			assertTrue(podcastEntities.contains(podcastList.get(i)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetPodcastsInvalidTema(){
		assertThrows(EntityNotFoundException.class, ()->{
			podcastTemaService.getPodcasts(0L);
		});
	}

	/**
	 * Prueba para consultar un autor de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPodcast() throws EntityNotFoundException, IllegalOperationException {
		PodcastEntity podcastEntity = podcastList.get(0);
		PodcastEntity podcast = podcastTemaService.getPodcast(tema.getId(), podcastEntity.getId());
		assertNotNull(podcast);

		assertEquals(podcastEntity.getId(), podcast.getId());
		assertEquals(podcastEntity.getTitulo(), podcast.getTitulo());
		assertEquals(podcastEntity.getImagen(), podcast.getImagen());
		assertEquals(podcastEntity.getCalificacion(), podcast.getCalificacion());
		assertEquals(podcastEntity.getDescripcion(), podcast.getDescripcion());
		assertEquals(podcastEntity.getPrecio(), podcast.getPrecio());

	}
	
	/**
	 * Prueba para consultar un autor que no existe de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidPodcast()  {
		assertThrows(EntityNotFoundException.class, ()->{
			podcastTemaService.getPodcast(tema.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un autor de un libro que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPodcastInvalidTema() {
		assertThrows(EntityNotFoundException.class, ()->{
			PodcastEntity podcastEntity = podcastList.get(0);
			podcastTemaService.getPodcast(0L, podcastEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un autor no asociado a un libro.
	 *
	 */
	@Test
	void testGetNotAssociatedPodcast() {
		assertThrows(IllegalOperationException.class, ()->{
			TemaEntity newTema = factory.manufacturePojo(TemaEntity.class);
			entityManager.persist(newTema);
			PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
			entityManager.persist(podcast);
			podcastTemaService.getPodcast(newTema.getId(), podcast.getId());
		});
	}

	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePodcasts() throws EntityNotFoundException {
		List<PodcastEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PodcastEntity entity = factory.manufacturePojo(PodcastEntity.class);
			entityManager.persist(entity);
			tema.getPodcasts().add(entity);
			nuevaLista.add(entity);
		}
		podcastTemaService.replacePodcasts(tema.getId(), nuevaLista);
		
		List<PodcastEntity> podcastEntities = podcastTemaService.getPodcasts(tema.getId());
		for (PodcastEntity aNuevaLista : nuevaLista) {
			assertTrue(podcastEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePodcasts2() throws EntityNotFoundException {
		List<PodcastEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PodcastEntity entity = factory.manufacturePojo(PodcastEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		podcastTemaService.replacePodcasts(tema.getId(), nuevaLista);
		
		List<PodcastEntity> podcastEntities = podcastTemaService.getPodcasts(tema.getId());
		for (PodcastEntity aNuevaLista : nuevaLista) {
			assertTrue(podcastEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los autores de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePodcastsInvalidTema(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<PodcastEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PodcastEntity entity = factory.manufacturePojo(PodcastEntity.class);
				entity.getTemas().add(tema);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			podcastTemaService.replacePodcasts(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los autores que no existen de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidPodcasts() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<PodcastEntity> nuevaLista = new ArrayList<>();
			PodcastEntity entity = factory.manufacturePojo(PodcastEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			podcastTemaService.replacePodcasts(tema.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un autor de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePodcastsInvalidPodcast(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<PodcastEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PodcastEntity entity = factory.manufacturePojo(PodcastEntity.class);
				entity.getTemas().add(tema);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			podcastTemaService.replacePodcasts(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro.
	 *
	 */
	@Test
	void testRemovePodcast() throws EntityNotFoundException {
		for (PodcastEntity podcast : podcastList) {
			podcastTemaService.removePodcast(tema.getId(), podcast.getId());
		}
		assertTrue(podcastTemaService.getPodcasts(tema.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un autor que no existe con un libro.
	 *
	 */
	@Test
	void testRemoveInvalidPodcast(){
		assertThrows(EntityNotFoundException.class, ()->{
			podcastTemaService.removePodcast(tema.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemovePodcastInvalidTema(){
		assertThrows(EntityNotFoundException.class, ()->{
			podcastTemaService.removePodcast(0L, podcastList.get(0).getId());
		});
	}

}