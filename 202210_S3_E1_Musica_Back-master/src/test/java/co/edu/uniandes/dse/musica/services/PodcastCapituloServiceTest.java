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

import co.edu.uniandes.dse.musica.entities.CapituloEntity;
import co.edu.uniandes.dse.musica.entities.PodcastEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Class to test PodcastCapituloService.class.
 *
 * @author Esteban Gonzalez Ruales
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PodcastCapituloService.class)
public class PodcastCapituloServiceTest {

    @Autowired
    private PodcastCapituloService podcastCapituloService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();
    private PodcastEntity podcast = new PodcastEntity();
    private List<CapituloEntity> capituloList = new ArrayList<CapituloEntity>();
    private Set<CapituloEntity> capituloSet = new HashSet<CapituloEntity>();

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
            CapituloEntity capituloEntity = factory.manufacturePojo(CapituloEntity.class);
            entityManager.persist(capituloEntity);
            capituloList.add(capituloEntity);
            capituloSet.add(capituloEntity);
        }
        Set<CapituloEntity> capituloSetClone = new HashSet<CapituloEntity>();
        capituloSetClone.addAll(capituloSet);
        podcast = factory.manufacturePojo(PodcastEntity.class);
        podcast.setCapitulos(capituloSetClone);
        entityManager.persist(podcast);
    }

    @Test
    void testAddAndGetCapituloToPodcast() throws EntityNotFoundException, IllegalOperationException {
        CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
        entityManager.persist(capitulo);
        podcastCapituloService.addCapituloToPodcast(podcast.getId(), capitulo.getId());
        CapituloEntity retrievedCapitulo = podcastCapituloService.getCapituloOfPodcast(podcast.getId(),
                capituloList.get(0).getId());
        assertEquals(capituloList.get(0).getId(), retrievedCapitulo.getId());
        assertEquals(capituloList.get(0).getTitulo(), retrievedCapitulo.getTitulo());
        assertEquals(capituloList.get(0).getImagen(), retrievedCapitulo.getImagen());
        assertEquals(capituloList.get(0).getDuracion(), retrievedCapitulo.getDuracion());
        assertEquals(capituloList.get(0).getFechaPublicacion(), retrievedCapitulo.getFechaPublicacion());
    }

    @Test
    void testAddInvalidCapituloToPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCapituloService.addCapituloToPodcast(podcast.getId(), 0L);
        });
    }

    @Test
    void testAddCapituloToInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCapituloService.addCapituloToPodcast(0L, capituloList.get(0).getId());
        });
    }

    @Test
    void testAddExisitingCapituloWithinPodcast() throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            capituloList.get(0).setTitulo("Capitulo");
            capituloList.get(1).setTitulo("Capitulo");
            podcastCapituloService.addCapituloToPodcast(podcast.getId(), capituloList.get(0).getId());
            podcastCapituloService.addCapituloToPodcast(podcast.getId(), capituloList.get(1).getId());
        });
    }

    @Test
    void testGetCapitulosOfPodcasts() throws EntityNotFoundException, IllegalOperationException {
        Set<CapituloEntity> capitulos = podcastCapituloService.getCapitulosOfPodcast(podcast.getId());
        assertEquals(capitulos.size(), capituloSet.size());
        capituloSet.forEach(capitulo -> assertTrue(capitulos.contains(capitulo)));
    }

    @Test
    void testGetCapitulosOfInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCapituloService.getCapitulosOfPodcast(0L);
        });
    }

    @Test
    void testGetCapituloOfPodcast() throws EntityNotFoundException, IllegalOperationException {
        CapituloEntity capitulo = capituloList.get(0);
        CapituloEntity retrievedCapitulo = podcastCapituloService.getCapituloOfPodcast(podcast.getId(),
                capitulo.getId());
        assertNotNull(retrievedCapitulo);
        assertEquals(capitulo.getId(), retrievedCapitulo.getId());
        assertEquals(capitulo.getTitulo(), retrievedCapitulo.getTitulo());
        assertEquals(capitulo.getImagen(), retrievedCapitulo.getImagen());
        assertEquals(capitulo.getDuracion(), retrievedCapitulo.getDuracion());
        assertEquals(capitulo.getFechaPublicacion(), retrievedCapitulo.getFechaPublicacion());
    }

    @Test
    void testGetInvalidCapituloOfPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCapituloService.getCapituloOfPodcast(podcast.getId(), 0L);
        });
    }

    @Test
    void testGetCapituloOfInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCapituloService.getCapituloOfPodcast(0L, capituloList.get(0).getId());
        });
    }

    @Test
    void testGetCapituloNotAsociatedToPodcast() {
        assertThrows(IllegalOperationException.class, () -> {
            CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
            entityManager.persist(capitulo);
            podcastCapituloService.getCapituloOfPodcast(podcast.getId(), capitulo.getId());
        });
    }

    @Test
    void testReplaceCapitulosOfPodcast() throws EntityNotFoundException, IllegalOperationException {
        Set<CapituloEntity> capitulos = new HashSet<CapituloEntity>();
        for (int i = 0; i < 5; i++) {
            CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
            entityManager.persist(capitulo);
            capitulos.add(capitulo);
        }

        podcastCapituloService.replaceCapitulosOfPodcast(podcast.getId(), capitulos);
        Set<CapituloEntity> retrievedCapitulos = podcastCapituloService.getCapitulosOfPodcast(podcast.getId());
        capitulos.forEach(capitulo -> assertTrue(retrievedCapitulos.contains(capitulo)));
    }

    @Test
    void testReplaceCapitulosOfPodcastWithDuplicateCreadores()
            throws EntityNotFoundException, IllegalOperationException {
        assertThrows(IllegalOperationException.class, () -> {
            Set<CapituloEntity> capitulos = new HashSet<CapituloEntity>();
            for (int i = 0; i < 5; i++) {
                CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
                capitulo.setTitulo("Capitulo");
                entityManager.persist(capitulo);
                capitulos.add(capitulo);
            }

            podcastCapituloService.replaceCapitulosOfPodcast(podcast.getId(), capitulos);
        });
    }

    @Test
    void testReplaceCapitulosOfInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<CapituloEntity> capitulos = new HashSet<CapituloEntity>();
            for (int i = 0; i < 5; i++) {
                CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
                entityManager.persist(capitulo);
                capitulos.add(capitulo);
            }

            podcastCapituloService.replaceCapitulosOfPodcast(0L, capitulos);
        });
    }

    @Test
    void testReplaceInvalidCapitulosOfPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<CapituloEntity> capitulos = new HashSet<CapituloEntity>();
            CapituloEntity capitulo = factory.manufacturePojo(CapituloEntity.class);
            capitulo.setId(0L);
            capitulos.add(capitulo);
            podcastCapituloService.replaceCapitulosOfPodcast(podcast.getId(), capitulos);
        });
    }

    @Test
    void testRemveCapituloOfPodcast() throws EntityNotFoundException, IllegalOperationException {
        podcastCapituloService.removeCapituloOfPodcast(podcast.getId(), capituloList.get(0).getId());
        assertFalse(podcastCapituloService.getCapitulosOfPodcast(podcast.getId()).contains(capituloList.get(0)));
    }

    @Test
    void testRemoveInvalidCapituloOfPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCapituloService.removeCapituloOfPodcast(podcast.getId(), 0L);
        });
    }

    @Test
    void testRemoveCapituloOfInvalidPodcast() {
        assertThrows(EntityNotFoundException.class, () -> {
            podcastCapituloService.removeCapituloOfPodcast(0L, capituloList.get(0).getId());
        });
    }
}