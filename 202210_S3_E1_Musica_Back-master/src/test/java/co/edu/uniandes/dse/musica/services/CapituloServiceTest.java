package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.CapituloEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
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
@Import(CapituloService.class)
class CapituloServiceTest {

    @Autowired
    private CapituloService capituloService;

    // @Autowired
    // private PodcastService podcastService;

    @Autowired
    private TestEntityManager entityManager;
    private PodamFactory factory = new PodamFactoryImpl();
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
        entityManager.getEntityManager().createQuery("delete from CapituloEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PodcastEntity").executeUpdate();
    }

    /**
     * Insert initial data for test.
     */
    private void insertData() {
        for (int i = 0; i < 3; i++) {
            CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
            entityManager.persist(capitulo);
            capituloList.add(capitulo);
        }

        for (int i = 0; i < 3; i++) {
            PodcastEntity podcast = factory.manufacturePojo(PodcastEntity.class);
            entityManager.persist(podcast);
            podcastList.add(podcast);
        }
    }

    /**
     * Test to list Capitulos.
     */
    @Test
    void testGetCapitulos() {
        List<CapituloEntity> list = capituloService.getCapitulos();
        assertEquals(list.size(), capituloList.size());
        for (CapituloEntity capitulo : list) {
            boolean found = false;

            for (CapituloEntity cap : capituloList) {
                if (capitulo.getId().equals(cap.getId()))
                    found = true;
            }

            assertTrue(found);
        }
    }

    @Test
    void testCreateCapitulo() throws EntityNotFoundException, IllegalOperationException {
        CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
        capitulo.setTitulo("Capitulo");
        capitulo.setPodcast(podcastList.get(0));

        CapituloEntity newCapitulo = capituloService.createCapitulo(capitulo);
        assertNotNull(newCapitulo);

        CapituloEntity retrieved = entityManager.find(CapituloEntity.class, newCapitulo.getId());
        assertEquals(capitulo.getId(), retrieved.getId());
        assertEquals(capitulo.getTitulo(), retrieved.getTitulo());
        assertEquals(capitulo.getImagen(), retrieved.getImagen());
        assertEquals(capitulo.getDuracion(), retrieved.getDuracion());
        assertEquals(capitulo.getFechaPublicacion(), retrieved.getFechaPublicacion());
        assertEquals(capitulo.getPodcast(), retrieved.getPodcast());

    }

    @Test
    void testValidCapituloName() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
            capitulo.setTitulo("");
            capituloService.createCapitulo(capitulo);
        });
    }

    /*@Test
    void testCapituloWithInvalidPodcast() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
            capitulo.setTitulo("Capitulo");
            capituloService.createCapitulo(capitulo);
        });
    }*/

    @Test
    void testGetCapitulo() throws EntityNotFoundException, IllegalOperationException {
        CapituloEntity capitulo = capituloList.get(0);
        CapituloEntity retrieved = capituloService.getCapitulo(capitulo.getId());
        assertNotNull(retrieved);
        assertEquals(capitulo.getId(), retrieved.getId());
        assertEquals(capitulo.getTitulo(), retrieved.getTitulo());
        assertEquals(capitulo.getImagen(), retrieved.getImagen());
        assertEquals(capitulo.getDuracion(), retrieved.getDuracion());
        assertEquals(capitulo.getFechaPublicacion(), retrieved.getFechaPublicacion());
        assertEquals(capitulo.getPodcast(), retrieved.getPodcast());
    }

    @Test
    void testGetInvalidCapitulo() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            capituloService.getCapitulo(0L);
        });
    }

    @Test
    void testUpdateCapitulo() throws EntityNotFoundException, IllegalOperationException {
        CapituloEntity capitulo = capituloList.get(0);
        CapituloEntity pojo = factory.manufacturePojo(CapituloEntity.class);
        capituloService.updateCapitulo(capitulo.getId(), pojo);

        CapituloEntity retrieved = entityManager.find(CapituloEntity.class, capitulo.getId());
        assertNotNull(retrieved);
        assertEquals(capitulo.getTitulo(), retrieved.getTitulo());
        assertEquals(capitulo.getImagen(), retrieved.getImagen());
        assertEquals(capitulo.getDuracion(), retrieved.getDuracion());
        assertEquals(capitulo.getFechaPublicacion(), retrieved.getFechaPublicacion());
        assertEquals(capitulo.getPodcast(), retrieved.getPodcast());
    }

    @Test
    void testUpdateInvalidCapitulo() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            CapituloEntity pojo = factory.manufacturePojo(CapituloEntity.class);
            pojo.setId(0L);
            capituloService.updateCapitulo(0L, pojo);
        });
    }

    @Test
    void testUpdateCapituloInvalidTitle() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            CapituloEntity capitulo = capituloList.get(0);
            CapituloEntity pojo = factory.manufacturePojo(CapituloEntity.class);
            pojo.setTitulo("");
            capituloService.updateCapitulo(capitulo.getId(), pojo);
        });
    }

    @Test
    void testDeleteCapitulo() throws EntityNotFoundException, IllegalOperationException {
        CapituloEntity capitulo = capituloList.get(1);
        capituloService.deleteCapitulo(capitulo.getId());
        CapituloEntity retrieved = entityManager.find(CapituloEntity.class, capitulo.getId());
        assertNull(retrieved);
    }

    @Test
    void testDeleteInvalidCapitulo() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(EntityNotFoundException.class, () -> {
            capituloService.deleteCapitulo(0L);
        });
    }
}