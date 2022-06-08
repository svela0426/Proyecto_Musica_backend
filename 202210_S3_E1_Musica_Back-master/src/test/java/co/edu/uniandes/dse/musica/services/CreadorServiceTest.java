package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
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

import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Tests for Creador.
 *
 * @author Esteban Gonzalez Ruales
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(CreadorService.class)
class CreadorServiceTest {

    @Autowired
    private CreadorService creadorService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<CreadorEntity> creadorList = new ArrayList<CreadorEntity>();
    private List<PodcastEntity> podcastList = new ArrayList<PodcastEntity>();

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
        entityManager.getEntityManager().createQuery("delete from CreadorEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PodcastEntity").executeUpdate();
    }

    /**
     * Insert initial data for test.
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            CreadorEntity creadorEntity = factory.manufacturePojo(CreadorEntity.class);
            entityManager.persist(creadorEntity);
            creadorList.add(creadorEntity);
        }

        for (int i = 0; i < 3; i++) {
            PodcastEntity podcastEntity = factory.manufacturePojo(PodcastEntity.class);
            entityManager.persist(podcastEntity);
            podcastList.add(podcastEntity);
        }
    }


    /**
     * Test to list Capitulos.
     */
    @Test
    void testGetCapitulos() {
        List<CreadorEntity> list = creadorService.getCreadores();
        assertEquals(list.size(), creadorList.size());
        for (CreadorEntity creador : list) {
            boolean found = false;

            for (CreadorEntity cap : creadorList) {
                if (creador.getId().equals(cap.getId()))
                    found = true;
            }

            assertTrue(found);
        }
    }

    @Test
    void testCreateCreador() throws EntityNotFoundException, IllegalOperationException {
        Set<PodcastEntity> podcasts = new HashSet<PodcastEntity>();
        podcastList.forEach(podcast -> podcasts.add(podcast));

        CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
        creador.setPodcasts(podcasts);

        CreadorEntity newCreador = creadorService.createCreador(creador);
        assertNotNull(newCreador);
        CreadorEntity retrieved = entityManager.find(CreadorEntity.class, newCreador.getId());
        assertEquals(creador.getId(), retrieved.getId());
        assertEquals(creador.getNombre(), retrieved.getNombre());
        assertEquals(creador.getNacionalidad(), retrieved.getNacionalidad());
        assertEquals(creador.getImagen(), retrieved.getImagen());
        assertEquals(creador.getPodcasts(), retrieved.getPodcasts());
    }

    @Test
    void testValidCreadorName() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
            creador.setNombre("");
            creadorService.createCreador(creador);
        });
    }


    @Test
    void testCreateExistingCreador() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
            creador.setNombre("creador");
            creadorService.createCreador(creador);
            creadorService.createCreador(creador);
        });
    }


    @Test
    void testGetCreador() throws EntityNotFoundException, IllegalOperationException {
        CreadorEntity creador = creadorList.get(0);
        CreadorEntity retrieved = creadorService.getCreador(creador.getId());
        assertNotNull(retrieved);
        assertEquals(creador.getId(), retrieved.getId());
        assertEquals(creador.getNombre(), retrieved.getNombre());
        assertEquals(creador.getNacionalidad(), retrieved.getNacionalidad());
        assertEquals(creador.getImagen(), retrieved.getImagen());
        assertEquals(creador.getPodcasts(), retrieved.getPodcasts());
    }

    @Test
    void testGetInvalidCreador() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorService.getCreador(0L);
        });
    }

    @Test
    void testUpdateCreador() throws EntityNotFoundException, IllegalOperationException {
        Set<PodcastEntity> podcasts = new HashSet<PodcastEntity>();
        podcastList.forEach(podcast -> podcasts.add(podcast));

        CreadorEntity creador = creadorList.get(0);
        CreadorEntity pojo = factory.manufacturePojo(CreadorEntity.class);
        pojo.setPodcasts(podcasts);
        pojo.setId(creador.getId());
        creadorService.updateCreador(creador.getId(), pojo);

        CreadorEntity retrieved = entityManager.find(CreadorEntity.class, creador.getId());
        assertNotNull(retrieved);
        assertEquals(creador.getId(), retrieved.getId());
        assertEquals(creador.getNombre(), retrieved.getNombre());
        assertEquals(creador.getNacionalidad(), retrieved.getNacionalidad());
        assertEquals(creador.getImagen(), retrieved.getImagen());
        assertEquals(creador.getPodcasts(), retrieved.getPodcasts());
    }

    @Test
    void testUpdateInvalidCreador() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            CreadorEntity pojo = factory.manufacturePojo(CreadorEntity.class);
            pojo.setId(0L);
            creadorService.updateCreador(0L, pojo);
        });
    }

    @Test
    void testUpdateCreadorInvalidName() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            CreadorEntity creador = creadorList.get(0);
            CreadorEntity pojo = factory.manufacturePojo(CreadorEntity.class);
            pojo.setNombre("");
            pojo.setId(creador.getId());
            creadorService.updateCreador(creador.getId(), pojo);
        });
    }

    @Test
    void testDeleteCreador() throws EntityNotFoundException, IllegalOperationException {
        CreadorEntity creador = creadorList.get(1);
        creadorService.deleteCreador(creador.getId());
        CreadorEntity retrieved = entityManager.find(CreadorEntity.class, creador.getId());
        assertNull(retrieved);
    }

    @Test
    void testDeleteInvalidCreador() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorService.deleteCreador(0L);
        });
    }
}