package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
 * Class to test CreadorPodcastService.class.
 *
 * @author Esteban Gonzalez Ruales
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(CreadorPodcastService.class)
public class CreadorPodcastServiceTest {

    @Autowired
    private CreadorPodcastService creadorPodcastService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private CreadorEntity creador = new CreadorEntity();
    private List<PodcastEntity> podcastList = new ArrayList<PodcastEntity>();
    private Set<PodcastEntity> podcastSet = new HashSet<PodcastEntity>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PodcastEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CreadorEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            PodcastEntity podcastEntity = factory.manufacturePojo(PodcastEntity.class);
            entityManager.persist(podcastEntity);
            podcastList.add(podcastEntity);
            podcastSet.add(podcastEntity);
        }

        Set<PodcastEntity> podcastSetClone = new HashSet<PodcastEntity>();
        podcastSetClone.addAll(podcastSet);
        creador = factory.manufacturePojo(CreadorEntity.class);
        creador.setPodcasts(podcastSetClone);
        entityManager.persist(creador);
    }

    @Test
    void testAddAndGetPodcastToCreador() throws EntityNotFoundException, IllegalOperationException {
        PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
        entityManager.persist(podcast);
        creadorPodcastService.addPodcastToCreador(creador.getId(), podcast.getId());
        PodcastEntity retrievedPodcast = creadorPodcastService.getPodcastOfCreador(creador.getId(),
                podcastList.get(0).getId());
        assertEquals(podcastList.get(0).getId(), retrievedPodcast.getId());
        assertEquals(podcastList.get(0).getTitulo(), retrievedPodcast.getTitulo());
        assertEquals(podcastList.get(0).getCalificacion(), retrievedPodcast.getCalificacion());
        assertEquals(podcastList.get(0).getImagen(), retrievedPodcast.getImagen());
        assertEquals(podcastList.get(0).getDescripcion(), retrievedPodcast.getDescripcion());
        assertEquals(podcastList.get(0).getPrecio(), retrievedPodcast.getPrecio());
    }

    @Test
    void testAddInvalidPodcastToCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorPodcastService.addPodcastToCreador(creador.getId(), 0L);
        });
    }

    @Test
    void testAddPodcastToInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorPodcastService.addPodcastToCreador(0L, podcastList.get(0).getId());
        });
    }

    @Test
    void testGetPodcastsOfCreador() throws EntityNotFoundException, IllegalOperationException {
        Set<PodcastEntity> podcasts = creadorPodcastService.getPodcastsOfCreador(creador.getId());
        assertEquals(podcasts.size(), podcastSet.size());
        podcastSet.forEach(podcast -> assertTrue(podcasts.contains(podcast)));
    }

    @Test
    void testGetPodcastsOfInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorPodcastService.getPodcastsOfCreador(0L);
        });
    }

    @Test
    void testGetPodcastOfCreador() throws EntityNotFoundException, IllegalOperationException {
        PodcastEntity podcast = podcastList.get(0);
        PodcastEntity retrievedPodcast = creadorPodcastService.getPodcastOfCreador(creador.getId(), podcast.getId());
        assertNotNull(retrievedPodcast);
        assertEquals(podcast.getId(), retrievedPodcast.getId());
        assertEquals(podcast.getTitulo(), retrievedPodcast.getTitulo());
        assertEquals(podcast.getCalificacion(), retrievedPodcast.getCalificacion());
        assertEquals(podcast.getImagen(), retrievedPodcast.getImagen());
        assertEquals(podcast.getDescripcion(), retrievedPodcast.getDescripcion());
        assertEquals(podcast.getPrecio(), retrievedPodcast.getPrecio());
    }

    @Test
    void testGetInvalidPodcastOfCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorPodcastService.getPodcastOfCreador(creador.getId(), 0L);
        });
    }

    @Test
    void testGetPodcastOfInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorPodcastService.getPodcastOfCreador(0L, podcastList.get(0).getId());
        });
    }

    @Test
    void testGetPodcastNotAsociatedToCreador() {
        assertThrows(IllegalOperationException.class, () -> {
            PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
            entityManager.persist(podcast);
            creadorPodcastService.getPodcastOfCreador(creador.getId(), podcast.getId());
        });
    }

    @Test
    void testReplacePodcastsOfCreador() throws EntityNotFoundException, IllegalOperationException {
        Set<PodcastEntity> podcasts = new HashSet<PodcastEntity>();
        for (int i = 0; i < 5; i++) {
            PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
            entityManager.persist(podcast);
            podcasts.add(podcast);
        }

        creadorPodcastService.replacePodcastsOfCreador(creador.getId(), podcasts);
        Set<PodcastEntity> retrievedPodcasts = creadorPodcastService.getPodcastsOfCreador(creador.getId());
        podcasts.forEach(podcast -> assertTrue(retrievedPodcasts.contains(podcast)));
    }

    @Test
    void testReplacePodcastsOfCreadorWithDuplicatePodcasts() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            Set<PodcastEntity> podcasts = new HashSet<PodcastEntity>();
            for (int i = 0; i < 5; i++) {
                PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
                podcast.setTitulo("Podcast");
                entityManager.persist(podcast);
                podcasts.add(podcast);
            }

            creadorPodcastService.replacePodcastsOfCreador(creador.getId(), podcasts);
        });
    }

    @Test
    void testReplacePodcastsOfInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<PodcastEntity> podcasts = new HashSet<PodcastEntity>();
            for (int i = 0; i < 5; i++) {
                PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
                entityManager.persist(podcast);
                podcasts.add(podcast);
            }

            creadorPodcastService.replacePodcastsOfCreador(0L, podcasts);
        });
    }

    @Test
    void testReplaceInvalidPodcastsOfCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<PodcastEntity> podcasts = new HashSet<PodcastEntity>();
            PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
            podcast.setId(0L);
            podcasts.add(podcast);
            creadorPodcastService.replacePodcastsOfCreador(creador.getId(), podcasts);
        });
    }

    @Test
    void testRemoveAllPodcastsOfCreador() throws EntityNotFoundException, IllegalOperationException {
        for (PodcastEntity podcast : podcastSet) {
            creadorPodcastService.removePodcastOfCreador(creador.getId(), podcast.getId());
        }

        assertTrue(creadorPodcastService.getPodcastsOfCreador(creador.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidPodcastOfCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorPodcastService.removePodcastOfCreador(creador.getId(), 0L);
        });
    }

    @Test
    void testRemovePodcastOfInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorPodcastService.removePodcastOfCreador(0L, podcastList.get(0).getId());
        });
    }
}