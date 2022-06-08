
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
 * Tests for Tema.
 *
 * @author Andres Parraga
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(TemaService.class)
class TemaServiceTest {

        @Autowired
        private TemaService temaService;

        @Autowired
        private TestEntityManager entityManager;

        private PodamFactory factory = new PodamFactoryImpl();

        private ArrayList<TemaEntity> temaList = new ArrayList<>();
        
        private ArrayList<PodcastEntity> podcastList = new ArrayList<>();

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
                entityManager.getEntityManager().createQuery("delete from TemaEntity").executeUpdate();
        }

        /**
         * Insert initial data for test.
         */
        private void insertData() {
                for (int i = 0; i < 3; i++) {
                        TemaEntity temaEntity = factory.manufacturePojo(TemaEntity.class);
                        
                        for (int j = 0; j < 3; j++) {
                        	//Crea 3 podcast que van a pertenecer al tema. 
                        	PodcastEntity podcastEntity = factory.manufacturePojo(PodcastEntity.class);
                        	entityManager.persist(podcastEntity);
                        	podcastList.add(podcastEntity);
			}

			temaEntity.setPodcasts(podcastList);
			entityManager.persist(temaEntity);
			temaList.add(temaEntity);
			
		}
	}      

        /**
         * Test to list Temas.
         * @throws IllegalOperationException 
         */

        @Test
	void testCreateTema() throws IllegalOperationException {
		//prueba la creacion de un nuevo tema
		TemaEntity newTema = factory.manufacturePojo(TemaEntity.class);
		newTema.setPodcasts(podcastList);
		TemaEntity result = temaService.createtema(newTema);
		assertNotNull(result);
		TemaEntity entity = entityManager.find(TemaEntity.class, result.getId());
		
		assertEquals(newTema.getId(), entity.getId());
		assertEquals(newTema.getNombre(), entity.getNombre());
		assertEquals(newTema.getPodcasts(), entity.getPodcasts());
	}
	
	@Test
	void testGetTemas() {
		//Prueba la funcion de obtener todos los temas
		List<TemaEntity> list = temaService.getTemas();
		assertEquals(temaList.size(), list.size());
		for (TemaEntity entity : list) {
			boolean found = false;
			for (TemaEntity storedEntity : temaList) {
				if (entity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}
	
	@Test
	void testGetTema() throws EntityNotFoundException {
		// prueba la funcion de obtener un tema por id
		TemaEntity entity = temaList.get(0);
		TemaEntity resultEntity = temaService.getTema(entity.getId());
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getNombre(), resultEntity.getNombre());
		assertEquals(resultEntity.getPodcasts(), entity.getPodcasts());
		
	}
	
	@Test
	void testGetTemaInvalid() {
		//prueba que tira error al buscar una entidad inexistente
		assertThrows(EntityNotFoundException.class,()->{
			temaService.getTema(0L);
		});
	}
	
	@Test
	void testUpdateTema() throws EntityNotFoundException {
		//prueba la funcion de actualizar un tema
		TemaEntity entity = temaList.get(0);
		TemaEntity pojoEntity = factory.manufacturePojo(TemaEntity.class);
		pojoEntity.setId(entity.getId());
		temaService.updateTema(entity.getId(), pojoEntity);
		
		TemaEntity resp = entityManager.find(TemaEntity.class, entity.getId());
		assertEquals(pojoEntity.getId(), resp.getId());
		assertEquals(pojoEntity.getNombre(), resp.getNombre());
		assertEquals(pojoEntity.getPodcasts(), resp.getPodcasts());
	}
	
	@Test
	void testUpdateTemaInvalid() {
		assertThrows(EntityNotFoundException.class, () -> {
			TemaEntity pojoEntity = factory.manufacturePojo(TemaEntity.class);
			pojoEntity.setId(0L);
			temaService.updateTema(0L, pojoEntity);
		});
	}
	
	@Test
	void testDeleteTema() throws EntityNotFoundException {
		TemaEntity entity = temaList.get(1);
		temaService.deleteTema(entity.getId());
		TemaEntity deleted = entityManager.find(TemaEntity.class, entity.getId());
		assertNull(deleted);
	}
	
	@Test
	void testBorrarTemaInvalido() {
		assertThrows(EntityNotFoundException.class, ()->{
			temaService.deleteTema(0L);
		});
	}
	
	

}