package co.edu.uniandes.dse.musica.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import co.edu.uniandes.dse.musica.entities.CapituloEntity;
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Tests for Capitulo.
 *
 * @author Esteban Gonzalez Ruales
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(EntityChecker.class)
class EntityCheckerTest {

    @Autowired
    private EntityChecker entityChecker;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<CapituloEntity> capituloList = new ArrayList<CapituloEntity>();
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
        entityManager.getEntityManager().createQuery("delete from CapituloEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CreadorEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PodcastEntity").executeUpdate();
    }

    /**
     * Insert initial data for test.
     */
    private void insertData() {
        int LIST_LENGTH = 20;

        for (int i = 0; i < LIST_LENGTH; i++) {
            CreadorEntity creadorEntity = factory.manufacturePojo(CreadorEntity.class);
            entityManager.persist(creadorEntity);
            creadorList.add(creadorEntity);
        }

        for (int i = 0; i < LIST_LENGTH; i++) {
            PodcastEntity podcastEntity = factory.manufacturePojo(PodcastEntity.class);
            int number = (int)Math.random() * LIST_LENGTH;
            podcastEntity.getCreadores().add(creadorList.get(number));
            //creadorList.get(number).getPodcasts().add(podcastEntity);
            entityManager.persist(podcastEntity);
            podcastList.add(podcastEntity);
        }

        for (PodcastEntity podcast : podcastList) {
        	for (CreadorEntity creador : podcast.getCreadores()) {
                creador.getPodcasts().add(podcast);
            }
        }

        for (int i = 0; i < LIST_LENGTH; i++) {
            CapituloEntity capituloEntity = factory.manufacturePojo(CapituloEntity.class);
            int number = (int)Math.random() * LIST_LENGTH;
            capituloEntity.setPodcast(podcastList.get(number));
            entityManager.persist(capituloEntity);
            capituloList.add(capituloEntity);
        }
    }

    @Test
    void testCheckCreadorExists() throws EntityNotFoundException {
        for (CreadorEntity creadorEntity : creadorList) {
            CreadorEntity creador = entityChecker.checkCreadorExists(creadorEntity.getId());
            assertNotNull(creador);
            assertEquals(creador.getId(), creadorEntity.getId());
            assertEquals(creador.getNombre(), creadorEntity.getNombre());
            assertEquals(creador.getNacionalidad(), creadorEntity.getNacionalidad());
            assertEquals(creador.getImagen(), creadorEntity.getImagen());
            creador.getPodcasts().forEach(podcast -> System.out.println(podcast.getTitulo()));
            System.out.println("\n\n\n\n\n\n\n\n\n");
            creadorEntity.getPodcasts().forEach(podcast -> System.out.println(podcast.getTitulo()));
            assertTrue(creador.getPodcasts().containsAll(creadorEntity.getPodcasts()));
        }
    }

    @Test
    void testCheckInvalidCreadorExists() {
        assertThrows(EntityNotFoundException.class, () -> {
            CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
            creador.setId(0L);
            entityChecker.checkCreadorExists(creador.getId());
        });
    }

    @Test
    void testCheckPodcastExists() throws EntityNotFoundException {
        for (PodcastEntity podcastEntity : podcastList) {
            PodcastEntity podcast = entityChecker.checkPodcastExists(podcastEntity.getId());
            assertEquals(podcast.getId(), podcastEntity.getId());
            assertEquals(podcast.getTitulo(), podcastEntity.getTitulo());
            assertEquals(podcast.getCalificacion(), podcastEntity.getCalificacion());
            assertEquals(podcast.getImagen(), podcastEntity.getImagen());
            assertEquals(podcast.getDescripcion(), podcastEntity.getDescripcion());
            assertEquals(podcast.getPrecio(), podcastEntity.getPrecio());
            assertTrue(podcast.getCapitulos().containsAll(podcastEntity.getCapitulos()));
            assertTrue(podcast.getCreadores().containsAll(podcastEntity.getCreadores()));
        }
    }

    @Test
    void testCheckInvalidPodcastExists() {
        assertThrows(EntityNotFoundException.class, () -> {
            PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
            podcast.setId(0L);
            entityChecker.checkPodcastExists(podcast.getId());
        });
    }

    @Test
    void testCheckCapituloExists() throws EntityNotFoundException {
        for (CapituloEntity capituloEntity : capituloList) {
            CapituloEntity capitulo = entityChecker.checkCapituloExists(capituloEntity.getId());
            assertEquals(capitulo.getId(), capituloEntity.getId());
            assertEquals(capitulo.getTitulo(), capituloEntity.getTitulo());
            assertEquals(capitulo.getImagen(), capituloEntity.getImagen());
            assertEquals(capitulo.getDuracion(), capituloEntity.getDuracion());
            assertEquals(capitulo.getFechaPublicacion(), capituloEntity.getFechaPublicacion());
            assertEquals(capitulo.getPodcast(), capituloEntity.getPodcast());
        }
    }

    @Test
    void testCheckInvalidCapituloExists() {
        assertThrows(EntityNotFoundException.class, () -> {
            CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
            capitulo.setId(0L);
            entityChecker.checkCapituloExists(capitulo.getId());
        });
    }
}