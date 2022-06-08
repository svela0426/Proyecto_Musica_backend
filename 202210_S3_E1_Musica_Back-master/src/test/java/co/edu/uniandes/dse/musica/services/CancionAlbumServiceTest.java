package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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

import co.edu.uniandes.dse.musica.entities.AlbumEntity;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(CancionAlbumService.class)
public class CancionAlbumServiceTest {

    @Autowired
    private CancionAlbumService cancionAlbumService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private CancionEntity cancion = new CancionEntity();
    private Set<AlbumEntity> albumList = new LinkedHashSet<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AlbumEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CancionEntity").executeUpdate();
    }

    private void insertData() {

        cancion = factory.manufacturePojo(CancionEntity.class);
        entityManager.persist(cancion);

        for (int i = 0; i < 3; i++) {
            AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(entity);
            entity.getCanciones().add(cancion);
            albumList.add(entity);
            cancion.getAlbumes().add(entity);
        }
    }

    @Test
    void testAddAlbum() throws EntityNotFoundException, IllegalOperationException {
        CancionEntity newCancion = factory.manufacturePojo(CancionEntity.class);
        entityManager.persist(newCancion);

        AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
        entityManager.persist(album);

        cancionAlbumService.addAlbum(album.getId(), newCancion.getId());

        AlbumEntity lastAlbum = cancionAlbumService.getAlbum(album.getId(), newCancion.getId());

        assertEquals(album.getId(), lastAlbum.getId());
        assertEquals(album.getTitulo(), lastAlbum.getTitulo());
        assertEquals(album.getImagen(), lastAlbum.getImagen());
    }

    @Test
    void testAddAlbumInvalidCancion() {
        assertThrows(EntityNotFoundException.class, () -> {
            AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(album);
            cancionAlbumService.addAlbum(album.getId(), 0L);
        });
    }

    @Test
	void testAddExistingAlbumTitleToCancion() {
		assertThrows(IllegalOperationException.class, () -> {
			ArrayList<AlbumEntity> listAlbums = new ArrayList<AlbumEntity>(albumList);
			listAlbums.get(0).setTitulo("Album"); 
			cancionAlbumService.addAlbum(listAlbums.get(0).getId(), cancion.getId()); 
            listAlbums.get(1).setTitulo("Album");
            cancionAlbumService.addAlbum(listAlbums.get(1).getId(), cancion.getId());  
        });
	}

    @Test
    void testGetAlbumes() throws EntityNotFoundException {
        Set<AlbumEntity> albumEntities = cancionAlbumService.getAlbumes(cancion.getId());

        assertEquals(albumList.size(), albumEntities.size());

        for (int i = 0; i < albumList.size(); i++) {
            assertTrue(albumEntities.contains(albumList.iterator().next()));
        }
    }

    @Test
    void testGetAlbumWithInvalidCancion() {
        assertThrows(EntityNotFoundException.class, () -> {
            cancionAlbumService.getAlbumes(0L);
        });
    }

    @Test
    void testGetAlbum() throws EntityNotFoundException, IllegalOperationException {
        AlbumEntity albumEntity = albumList.iterator().next();
        AlbumEntity album = cancionAlbumService.getAlbum(albumEntity.getId(), cancion.getId());
        assertNotNull(album);

        assertEquals(albumEntity.getId(), album.getId());
        assertEquals(albumEntity.getTitulo(), album.getTitulo());
        assertEquals(albumEntity.getImagen(), album.getImagen());
    }

    @Test
    void testGetInvalidAlbum() {
        assertThrows(EntityNotFoundException.class, () -> {
            cancionAlbumService.getAlbum(0L, cancion.getId());
        });
    }

    @Test
    void testGetAlbumInvalidCancion() {
        assertThrows(EntityNotFoundException.class, () -> {
            AlbumEntity albumEntity = albumList.iterator().next();
            cancionAlbumService.getAlbum(albumEntity.getId(), 0L);
        });
    }

    @Test
    void testGetNotAssociatedAlbum() {
        assertThrows(IllegalOperationException.class, () -> {
            CancionEntity newCancion = factory.manufacturePojo(CancionEntity.class);
            entityManager.persist(newCancion);
            AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(album);
            cancionAlbumService.getAlbum(album.getId(), newCancion.getId());
        });
    }

    @Test
    void testReplaceAlbumes() throws EntityNotFoundException {
        Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
        for (int i = 0; i < 3; i++) {
            AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(entity);
            cancion.getAlbumes().add(entity);
            nuevaLista.add(entity);
        }
        cancionAlbumService.replaceAlbumes(cancion.getId(), nuevaLista);

        Set<AlbumEntity> albumEntities = cancionAlbumService.getAlbumes(cancion.getId());
        for (AlbumEntity aNuevaLista : nuevaLista) {
            assertTrue(albumEntities.contains(aNuevaLista));
        }
    }

    @Test
    void testReplaceAlbumes2() throws EntityNotFoundException {
        Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
        for (int i = 0; i < 3; i++) {
            AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(entity);
            nuevaLista.add(entity);
        }
        cancionAlbumService.replaceAlbumes(cancion.getId(), nuevaLista);

        Set<AlbumEntity> albumEntities = cancionAlbumService.getAlbumes(cancion.getId());
        for (AlbumEntity aNuevaLista : nuevaLista) {
            assertTrue(albumEntities.contains(aNuevaLista));
        }
    }

    @Test
    void testReplaceAlbumesInvalidCancion() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
            for (int i = 0; i < 3; i++) {
                AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
                entity.getCanciones().add(cancion);
                entityManager.persist(entity);
                nuevaLista.add(entity);
            }
            cancionAlbumService.replaceAlbumes(0L, nuevaLista);
        });
    }

    @Test
    void testReplaceInvalidAlbumes() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
            AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
            entity.setId(0L);
            nuevaLista.add(entity);
            cancionAlbumService.replaceAlbumes(0L, nuevaLista);
        });
    }

    @Test
    void testReplaceAlbumesInvalidAlbum() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
            for (int i = 0; i < 3; i++) {
                AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
                entity.getCanciones().add(cancion);
                entityManager.persist(entity);
                nuevaLista.add(entity);
            }
            cancionAlbumService.replaceAlbumes(0L, nuevaLista);
        });
    }

    @Test
    void testRemoveAlbum() throws EntityNotFoundException {
        for (AlbumEntity album : albumList) {
            cancionAlbumService.removeAlbum(album.getId(), cancion.getId());
        }

        assertTrue(cancionAlbumService.getAlbumes(cancion.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidAlbum() {
        assertThrows(EntityNotFoundException.class, () -> {
            cancionAlbumService.removeAlbum(0L, cancion.getId());
        });
    }

    @Test
    void testRemoveAlbumInvalidCancion() {
        assertThrows(EntityNotFoundException.class, () -> {
            cancionAlbumService.removeAlbum(albumList.iterator().next().getId(), 0L);
        });
    }

}
