package co.edu.uniandes.dse.musica.services;

import static org.junit.jupiter.api.Assertions.*;

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
import co.edu.uniandes.dse.musica.entities.CreadorEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(CreadorAlbumService.class)
public class CreadorAlbumServiceTest {
    @Autowired
    private CreadorAlbumService creadorAlbumService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private CreadorEntity creador = new CreadorEntity();
    private Set<AlbumEntity> albumList = new LinkedHashSet<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from AlbumEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CreadorEntity").executeUpdate();
    }

    private void insertData() {

        creador = factory.manufacturePojo(CreadorEntity.class);
        entityManager.persist(creador);

        for (int i = 0; i < 3; i++) {
            AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(entity);
            entity.getArtistas().add(creador);
            albumList.add(entity);
            creador.getAlbumes().add(entity);
        }
    }

    @Test
    void testAddAlbum() throws EntityNotFoundException, IllegalOperationException {
        CreadorEntity newCreador = factory.manufacturePojo(CreadorEntity.class);
        entityManager.persist(newCreador);

        AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
        entityManager.persist(album);

        creadorAlbumService.addAlbum(album.getId(), newCreador.getId());

        AlbumEntity lastAlbum = creadorAlbumService.getAlbum(album.getId(), newCreador.getId());

        assertEquals(album.getId(), lastAlbum.getId());
        assertEquals(album.getTitulo(), lastAlbum.getTitulo());
        assertEquals(album.getImagen(), lastAlbum.getImagen());
    }

    @Test
    void testAddAlbumInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(album);
            creadorAlbumService.addAlbum(album.getId(), 0L);
        });
    }

	@Test
	void testAddAlbumWithRepeatedName() {
		assertThrows(IllegalOperationException.class, ()->{
			AlbumEntity album0 = factory.manufacturePojo(AlbumEntity.class);
            album0.setTitulo("Album");
			entityManager.persist(album0);

			AlbumEntity album1 = factory.manufacturePojo(AlbumEntity.class);
            album1.setTitulo("Album");
			entityManager.persist(album1);

			
			creadorAlbumService.addAlbum(album0.getId(), creador.getId());
			creadorAlbumService.addAlbum(album1.getId(), creador.getId());
		});
	}

    @Test
	void testAddInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, ()-> {
            creadorAlbumService.addAlbum(0L, creador.getId());
        });
	}

    @Test
    void testGetAlbumes() throws EntityNotFoundException {
        Set<AlbumEntity> albumEntities = creadorAlbumService.getAlbumes(creador.getId());

        assertEquals(albumList.size(), albumEntities.size());

        for (int i = 0; i < albumList.size(); i++) {
            assertTrue(albumEntities.contains(albumList.iterator().next()));
        }
    }

    @Test
    void testGetAlbumWithInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorAlbumService.getAlbumes(0L);
        });
    }

    @Test
    void testGetAlbum() throws EntityNotFoundException, IllegalOperationException {
        AlbumEntity albumEntity = albumList.iterator().next();
        AlbumEntity album = creadorAlbumService.getAlbum(albumEntity.getId(), creador.getId());
        assertNotNull(album);

        assertEquals(albumEntity.getId(), album.getId());
        assertEquals(albumEntity.getTitulo(), album.getTitulo());
        assertEquals(albumEntity.getImagen(), album.getImagen());
    }

    @Test
    void testGetInvalidAlbum() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorAlbumService.getAlbum(0L, creador.getId());
        });
    }

    @Test
    void testGetAlbumInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            AlbumEntity albumEntity = albumList.iterator().next();
            creadorAlbumService.getAlbum(albumEntity.getId(), 0L);
        });
    }

    @Test
    void testGetNotAssociatedAlbum() {
        assertThrows(IllegalOperationException.class, () -> {
            CreadorEntity newCreador = factory.manufacturePojo(CreadorEntity.class);
            entityManager.persist(newCreador);
            AlbumEntity album = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(album);
            creadorAlbumService.getAlbum(album.getId(), newCreador.getId());
        });
    }

    @Test
    void testReplaceAlbumes() throws EntityNotFoundException {
        Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
        for (int i = 0; i < 3; i++) {
            AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
            entityManager.persist(entity);
            creador.getAlbumes().add(entity);
            nuevaLista.add(entity);
        }
        creadorAlbumService.replaceAlbumes(creador.getId(), nuevaLista);

        Set<AlbumEntity> albumEntities = creadorAlbumService.getAlbumes(creador.getId());
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
        creadorAlbumService.replaceAlbumes(creador.getId(), nuevaLista);

        Set<AlbumEntity> albumEntities = creadorAlbumService.getAlbumes(creador.getId());
        for (AlbumEntity aNuevaLista : nuevaLista) {
            assertTrue(albumEntities.contains(aNuevaLista));
        }
    }

    @Test
    void testReplaceAlbumesInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
            for (int i = 0; i < 3; i++) {
                AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
                entity.getArtistas().add(creador);
                entityManager.persist(entity);
                nuevaLista.add(entity);
            }
            creadorAlbumService.replaceAlbumes(0L, nuevaLista);
        });
    }

    @Test
    void testReplaceInvalidAlbumes() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
            AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
            entity.setId(0L);
            nuevaLista.add(entity);
            creadorAlbumService.replaceAlbumes(0L, nuevaLista);
        });
    }

    @Test
    void testReplaceAlbumesInvalidAlbum() {
        assertThrows(EntityNotFoundException.class, () -> {
            Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
            for (int i = 0; i < 3; i++) {
                AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
                entity.getArtistas().add(creador);
                entityManager.persist(entity);
                nuevaLista.add(entity);
            }
            creadorAlbumService.replaceAlbumes(0L, nuevaLista);
        });
    }

    @Test
	void testReplaceInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, ()-> {
            Set<AlbumEntity> nuevaLista = new LinkedHashSet<>();
			AlbumEntity entity = factory.manufacturePojo(AlbumEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			creadorAlbumService.replaceAlbumes(creador.getId(), nuevaLista);			
        });
	}

    @Test
    void testRemoveAlbum() throws EntityNotFoundException {
        for (AlbumEntity album : albumList) {
            creadorAlbumService.removeAlbum(album.getId(), creador.getId());
        }

        assertTrue(creadorAlbumService.getAlbumes(creador.getId()).isEmpty());
    }

    @Test
    void testRemoveInvalidAlbum() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorAlbumService.removeAlbum(0L, creador.getId());
        });
    }

    @Test
    void testRemoveAlbumInvalidCreador() {
        assertThrows(EntityNotFoundException.class, () -> {
            creadorAlbumService.removeAlbum(albumList.iterator().next().getId(), 0L);
        });
    }

}

