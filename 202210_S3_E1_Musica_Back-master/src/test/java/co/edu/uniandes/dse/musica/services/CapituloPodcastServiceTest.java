package co.edu.uniandes.dse.musica.services;

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
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Class to test CapituloPodcastService.class.
 *
 * @author Esteban Gonzalez Ruales
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(CapituloPodcastService.class)
public class CapituloPodcastServiceTest {

    @Autowired
    private CapituloPodcastService capituloPodcastService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private CapituloEntity capitulo = new CapituloEntity();
    private List<PodcastEntity> podcastList = new ArrayList<PodcastEntity>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from CapituloEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PodcastEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            PodcastEntity podcastEntity = factory.manufacturePojo(PodcastEntity.class);
            entityManager.persist(podcastEntity);
            podcastList.add(podcastEntity);
        }

        capitulo = factory.manufacturePojo(CapituloEntity.class);
        capitulo.setPodcast(podcastList.get(0));
        entityManager.persist(capitulo);
    }

    @Test
    void testGetPodcastOfCapitulo() throws EntityNotFoundException {
        PodcastEntity retrievedCapitulo = capituloPodcastService.getPodcastOfCapitulo(capitulo.getId());
        assertNotNull(retrievedCapitulo);
        assertEquals(podcastList.get(0).getId(), retrievedCapitulo.getId());
        assertEquals(podcastList.get(0).getTitulo(), retrievedCapitulo.getTitulo());
        assertEquals(podcastList.get(0).getCalificacion(), retrievedCapitulo.getCalificacion());
        assertEquals(podcastList.get(0).getImagen(), retrievedCapitulo.getImagen());
        assertEquals(podcastList.get(0).getDescripcion(), retrievedCapitulo.getDescripcion());
        assertEquals(podcastList.get(0).getPrecio(), retrievedCapitulo.getPrecio());
    }

    @Test
    void testGetPodcastOfInvalidCapitulo() {
        assertThrows(EntityNotFoundException.class, () -> {
            capituloPodcastService.getPodcastOfCapitulo(0L);
        });
    }

    @Test
    void testSetPodcastOfCapitulo() throws EntityNotFoundException, IllegalOperationException {
        capituloPodcastService.setPodcastOfCapitulo(capitulo.getId(), podcastList.get(1).getId());
        PodcastEntity retrievedPodcast = capituloPodcastService.getPodcastOfCapitulo(capitulo.getId());
        assertNotNull(retrievedPodcast);
        assertTrue(retrievedPodcast == podcastList.get(1));
    }

    @Test
    void testSetPodcastOfInvalidCapitulo() {
        assertThrows(EntityNotFoundException.class, () -> {
            capituloPodcastService.setPodcastOfCapitulo(0L, podcastList.get(1).getId());
        });
    }

    @Test
    void testSetInvalidPodcastOfCapitulo() {
        assertThrows(EntityNotFoundException.class, () -> {
            capituloPodcastService.setPodcastOfCapitulo(capitulo.getId(), 0L);
        });
    }
}