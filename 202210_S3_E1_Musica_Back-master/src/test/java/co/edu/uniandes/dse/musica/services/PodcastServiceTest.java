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

import co.edu.uniandes.dse.musica.entities.CapituloEntity;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Tests for Podcast.
 *
 * @author Esteban Gonzalez Ruales
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PodcastService.class)
class PodcastServiceTest {

    @Autowired
    private PodcastService podcastService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<CreadorEntity> creadorList = new ArrayList<CreadorEntity>();
    private List<PodcastEntity> podcastList = new ArrayList<PodcastEntity>();
    private List<CapituloEntity> capituloList = new ArrayList<CapituloEntity>();

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
        entityManager.getEntityManager().createQuery("delete from PodcastEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CreadorEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CapituloEntity").executeUpdate();
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

        for (int i = 0; i < 3; i++) {
            CapituloEntity capituloEntity = factory.manufacturePojo(CapituloEntity.class);
            entityManager.persist(capituloEntity);
            capituloList.add(capituloEntity);
        }
    }

    /**
     * Test to list Podcasts.
     */
    @Test
    void testGetPodcasts() {
        List<PodcastEntity> list = podcastService.getPodcasts();
        assertEquals(list.size(), podcastList.size());
        for (PodcastEntity podcast : list) {
            boolean found = false;

            for (PodcastEntity podc : podcastList) {
                if (podcast.getId().equals(podc.getId()))
                    found = true;
            }

            assertTrue(found);
        }
    }

    @Test
    void testCreatePodcast() throws EntityNotFoundException, IllegalOperationException {
        Set<CapituloEntity> capitulos = new HashSet<CapituloEntity>();
        capituloList.forEach(capitulo -> capitulos.add(capitulo));
        Set<CreadorEntity> creadores = new HashSet<CreadorEntity>();
        creadorList.forEach(creador -> creadores.add(creador));

        PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
        podcast.setCapitulos(capitulos);
        podcast.setCreadores(creadores);

        PodcastEntity newPodcast = podcastService.createPodcast(podcast);
        assertNotNull(newPodcast);

        PodcastEntity retrieved = entityManager.find(PodcastEntity.class, newPodcast.getId());
        assertEquals(podcast.getId(), retrieved.getId());
        assertEquals(podcast.getTitulo(), retrieved.getTitulo());
        assertEquals(podcast.getCalificacion(), retrieved.getCalificacion());
        assertEquals(podcast.getImagen(), retrieved.getImagen());
        assertEquals(podcast.getDescripcion(), retrieved.getDescripcion());
        assertEquals(podcast.getPrecio(), retrieved.getPrecio());
        assertEquals(podcast.getCapitulos(), retrieved.getCapitulos());
        assertEquals(podcast.getCreadores(), retrieved.getCreadores());
    }

    @Test
    void testValidPodcastName() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
            podcast.setTitulo("");
            podcastService.createPodcast(podcast);
        });
    }

    @Test
    void testGetPodcast() throws EntityNotFoundException, IllegalOperationException {
        PodcastEntity podcast = podcastList.get(0);
        PodcastEntity retrieved = podcastService.getPodcast(podcast.getId());
        assertNotNull(retrieved);
        assertEquals(podcast.getId(), retrieved.getId());
        assertEquals(podcast.getTitulo(), retrieved.getTitulo());
        assertEquals(podcast.getCalificacion(), retrieved.getCalificacion());
        assertEquals(podcast.getImagen(), retrieved.getImagen());
        assertEquals(podcast.getDescripcion(), retrieved.getDescripcion());
        assertEquals(podcast.getPrecio(), retrieved.getPrecio());
        assertEquals(podcast.getCapitulos(), retrieved.getCapitulos());
        assertEquals(podcast.getCreadores(), retrieved.getCreadores());
    }

    @Test
    void testGetInvalidPodcast() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastService.getPodcast(0L);
        });
    }

    @Test
    void testUpdatePodcast() throws EntityNotFoundException, IllegalOperationException {
        Set<CapituloEntity> capitulos = new HashSet<CapituloEntity>();
        capituloList.forEach(capitulo -> capitulos.add(capitulo));
        Set<CreadorEntity> creadores = new HashSet<CreadorEntity>();
        creadorList.forEach(creador -> creadores.add(creador));

        PodcastEntity podcast = podcastList.get(0);
        PodcastEntity pojo = factory.manufacturePojo(PodcastEntity.class);

        pojo.setCreadores(creadores);
        pojo.setCapitulos(capitulos);

        pojo.setId(podcast.getId());
        podcastService.updatePodcast(podcast.getId(), pojo);

        PodcastEntity retrieved = entityManager.find(PodcastEntity.class, podcast.getId());
        assertNotNull(retrieved);
        assertEquals(podcast.getId(), retrieved.getId());
        assertEquals(podcast.getTitulo(), retrieved.getTitulo());
        assertEquals(podcast.getCalificacion(), retrieved.getCalificacion());
        assertEquals(podcast.getImagen(), retrieved.getImagen());
        assertEquals(podcast.getDescripcion(), retrieved.getDescripcion());
        assertEquals(podcast.getPrecio(), retrieved.getPrecio());
        assertEquals(podcast.getCapitulos(), retrieved.getCapitulos());
        assertEquals(podcast.getCreadores(), retrieved.getCreadores());
    }

    @Test
    void testUpdateInvalidPodcast() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            PodcastEntity pojo = factory.manufacturePojo(PodcastEntity.class);
            pojo.setId(0L);
            podcastService.updatePodcast(0L, pojo);
        });
    }

    /*
     * @Test
     * void testUpdatePodcastWithInvalidCreador() {
     * assertThrows(EntityNotFoundException.class, () -> {
     * PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
     * CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
     * creador.setId(0L);
     * podcast.setTitulo("Podcast");
     * podcast.getCreadores().add(creador);
     * podcastService.updatePodcast(podcastList.get(0).getId(), podcast);
     * });
     * }
     */

    /*
     * @Test
     * void testUpdatePodcastWithInvalidCapitulo() throws EntityNotFoundException {
     * assertThrows(EntityNotFoundException.class, () -> {
     * PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
     * CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
     * capitulo.setId(0L);
     * podcast.setTitulo("Podcast");
     * podcast.getCapitulos().add(capitulo);
     * podcastService.updatePodcast(podcastList.get(0).getId(), podcast);
     * });
     * }
     */

    @Test
    void testUpdatePodcastInvalidTitle() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            PodcastEntity podcast = podcastList.get(0);
            PodcastEntity pojo = factory.manufacturePojo(PodcastEntity.class);
            pojo.setTitulo("");
            pojo.setId(podcast.getId());
            podcastService.updatePodcast(podcast.getId(), pojo);
        });
    }

    @Test
    void testDeletePodcast() throws EntityNotFoundException, IllegalOperationException {
        PodcastEntity podcast = podcastList.get(1);
        podcastService.deletePodcast(podcast.getId());
        PodcastEntity retrieved = entityManager.find(PodcastEntity.class, podcast.getId());
        assertNull(retrieved);
    }

    @Test
    void testDeleteInvalidPodcast() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastService.deletePodcast(0L);
        });
    }

}