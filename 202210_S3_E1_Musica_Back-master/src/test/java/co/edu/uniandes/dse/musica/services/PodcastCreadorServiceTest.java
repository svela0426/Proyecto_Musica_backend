package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
 * Class to test PodcastCreadorService.class.
 *
 * @author Esteban Gonzalez Ruales
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PodcastCreadorService.class)
public class PodcastCreadorServiceTest {

    @Autowired
    private PodcastCreadorService podcastCreadorService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private PodcastEntity podcast = new PodcastEntity();
    private List<CreadorEntity> creadorList = new ArrayList<CreadorEntity>();
    private Set<CreadorEntity> creadorSet = new HashSet<CreadorEntity>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from CreadorEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PodcastEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            CreadorEntity creadorEntity = factory.manufacturePojo(CreadorEntity.class);
            entityManager.persist(creadorEntity);
            creadorList.add(creadorEntity);
            creadorSet.add(creadorEntity);
        }

        Set<CreadorEntity> creadorSetClone = new HashSet<CreadorEntity>();
        creadorSetClone.addAll(creadorSet);
        podcast = factory.manufacturePojo(PodcastEntity.class);
        podcast.setCreadores(creadorSetClone);
        entityManager.persist(podcast);
    }

    @Test
    void testAddAndGetCreadorToPodcast() throws EntityNotFoundException, IllegalOperationException {
        CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
        entityManager.persist(creador);
        podcastCreadorService.addCreadorToPodcast(podcast.getId(), creador.getId());
        CreadorEntity retrievedCreador = podcastCreadorService.getCreadorOfPodcast(podcast.getId(),
                creadorList.get(0).getId());
        assertEquals(creadorList.get(0).getId(), retrievedCreador.getId());
        assertEquals(creadorList.get(0).getNombre(), retrievedCreador.getNombre());
        assertEquals(creadorList.get(0).getNacionalidad(), retrievedCreador.getNacionalidad());
        assertEquals(creadorList.get(0).getImagen(), retrievedCreador.getImagen());
    }

    @Test
    void testAddInvalidCreadorToPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCreadorService.addCreadorToPodcast(podcast.getId(), 0L);
        });
    }

    @Test
    void testAddCreadorToInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCreadorService.addCreadorToPodcast(0L, creadorList.get(0).getId());
        });
    }

    @Test
    void testGetCreadoresOfPodcasts() throws EntityNotFoundException, IllegalOperationException {
        Set<CreadorEntity> creadores = podcastCreadorService.getCreadoresOfPodcast(podcast.getId());
        assertEquals(creadores.size(), creadorSet.size());
        creadorSet.forEach(creador -> assertTrue(creadores.contains(creador)));
    }

    @Test
    void testGetCreadoresOfInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCreadorService.getCreadoresOfPodcast(0L);
        });
    }

    @Test
    void testGetCreadorOfPodcast() throws EntityNotFoundException, IllegalOperationException {
        CreadorEntity creador = creadorList.get(0);
        CreadorEntity retrievedCreador = podcastCreadorService.getCreadorOfPodcast(podcast.getId(), creador.getId());
        assertNotNull(retrievedCreador);
        assertEquals(creador.getId(), retrievedCreador.getId());
        assertEquals(creador.getNombre(), retrievedCreador.getNombre());
        assertEquals(creador.getNacionalidad(), retrievedCreador.getNacionalidad());
        assertEquals(creador.getImagen(), retrievedCreador.getImagen());
    }

    @Test
    void testGetInvalidCreadorOfPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCreadorService.getCreadorOfPodcast(podcast.getId(), 0L);
        });
    }

    @Test
    void testGetCreadorOfInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCreadorService.getCreadorOfPodcast(0L, creadorList.get(0).getId());
        });
    }

    @Test
    void testGetCreadorNotAsociatedToPodcast() {
        assertThrows(IllegalOperationException.class, () -> {
            CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
            entityManager.persist(creador);
            podcastCreadorService.getCreadorOfPodcast(podcast.getId(), creador.getId());
        });
    }

    @Test
    void testReplaceCreadoresOfPodcast() throws EntityNotFoundException, IllegalOperationException {
        Set<CreadorEntity> creadores = new HashSet<CreadorEntity>();
        for (int i = 0; i < 5; i++) {
            CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
            entityManager.persist(creador);
            creadores.add(creador);
        }

        podcastCreadorService.replaceCreadoresOfPodcast(podcast.getId(), creadores);
        Set<CreadorEntity> retrievedCreadores = podcastCreadorService.getCreadoresOfPodcast(podcast.getId());
        creadores.forEach(creador -> assertTrue(retrievedCreadores.contains(creador)));
    }

    @Test
    void testReplaceCreadoresOfPodcastWithDuplicateCreadores()
            throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            Set<CreadorEntity> creadores = new HashSet<CreadorEntity>();
            for (int i = 0; i < 5; i++) {
                CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
                creador.setNombre("Creador");
                entityManager.persist(creador);
                creadores.add(creador);
            }

            podcastCreadorService.replaceCreadoresOfPodcast(podcast.getId(), creadores);
        });
    }

    @Test
    void testReplaceCreadoresOfInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<CreadorEntity> creadores = new HashSet<CreadorEntity>();
            for (int i = 0; i < 5; i++) {
                CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
                entityManager.persist(creador);
                creadores.add(creador);
            }

            podcastCreadorService.replaceCreadoresOfPodcast(0L, creadores);
        });
    }

    @Test
    void testReplaceInvalidCreadoresOfPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<CreadorEntity> creadores = new HashSet<CreadorEntity>();
            CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
            creador.setId(0L);
            creadores.add(creador);
            podcastCreadorService.replaceCreadoresOfPodcast(podcast.getId(), creadores);
        });
    }

    @Test
    void testRemoveCreadorOfPodcast() throws EntityNotFoundException, IllegalOperationException {
        podcastCreadorService.removeCreadorOfPodcast(podcast.getId(), creadorList.get(0).getId());
        assertFalse(podcastCreadorService.getCreadoresOfPodcast(podcast.getId()).contains(creadorList.get(0)));
    }

    @Test
    void testRemoveAllCreadoresOfPodcast() {
        assertThrows(IllegalOperationException.class, () -> {
            for (CreadorEntity creador : creadorSet) {
                podcastCreadorService.removeCreadorOfPodcast(podcast.getId(), creador.getId());
            }
        });
    }

    @Test
    void testRemoveInvalidCreadorOfPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCreadorService.removeCreadorOfPodcast(podcast.getId(), 0L);
        });
    }

    @Test
    void testRemoveCreadorOfInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCreadorService.removeCreadorOfPodcast(0L, creadorList.get(0).getId());
        });
    }
}