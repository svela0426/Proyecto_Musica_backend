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
@Import(AlbumCreadorService.class)
public class AlbumCreadorServiceTest {

    @Autowired
    private AlbumCreadorService albumCreadorService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private AlbumEntity album = new AlbumEntity();
    private Set<CreadorEntity> creadorList = new LinkedHashSet<>();

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

        album = factory.manufacturePojo(AlbumEntity.class);
        entityManager.persist(album);

        for(int i = 0; i < 3; i++) {
            CreadorEntity entity = factory.manufacturePojo(CreadorEntity.class);
            entityManager.persist(entity);
            entity.getAlbumes().add(album);
            creadorList.add(entity);
            album.getArtistas().add(entity);
        }
    }

    @Test
    void testAddCreador() throws EntityNotFoundException, IllegalOperationException {
        
		AlbumEntity newAlbum = factory.manufacturePojo(AlbumEntity.class);
        entityManager.persist(newAlbum);

        CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
        entityManager.persist(creador);

        albumCreadorService.addCreador(newAlbum.getId(), creador.getId());

        CreadorEntity lastCreador = albumCreadorService.getCreador(newAlbum.getId(), creador.getId());

        assertEquals(creador.getId(), lastCreador.getId());
        assertEquals(creador.getNombre(), lastCreador.getNombre());
        assertEquals(creador.getNacionalidad(), lastCreador.getNacionalidad());
        assertEquals(creador.getImagen(), lastCreador.getImagen());
    }

    @Test
    void testAddCreadorInvalidAlbum() {
        assertThrows(EntityNotFoundException.class, ()-> {
            CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
            entityManager.persist(creador);
            albumCreadorService.addCreador(0L, creador.getId());
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

			CreadorEntity creador = creadorList.iterator().next(); 
			
			albumCreadorService.addCreador(album0.getId(), creador.getId());
			albumCreadorService.addCreador(album1.getId(), creador.getId());
		});
	}

	@Test
	void testAddInvalidCreador() {
		assertThrows(EntityNotFoundException.class, ()-> {
            albumCreadorService.addCreador(album.getId(), 0L);
        });
	}


    @Test
	void testGetArtistas() throws EntityNotFoundException {
		Set<CreadorEntity> creadorEntities = albumCreadorService.getArtistas(album.getId());

		assertEquals(creadorList.size(), creadorEntities.size());

		for (int i = 0; i < creadorList.size(); i++) {
			assertTrue(creadorEntities.contains(creadorList.iterator().next()));
		}
	}

    @Test
	void testGetCreadorWithInvalidAlbum(){
		assertThrows(EntityNotFoundException.class, ()->{
			albumCreadorService.getArtistas(0L);
		});
	}


    @Test
	void testGetCreador() throws EntityNotFoundException, IllegalOperationException {
		CreadorEntity creadorEntity = creadorList.iterator().next();
		CreadorEntity creador = albumCreadorService.getCreador(album.getId(),creadorEntity.getId());
		assertNotNull(creador);

        assertEquals(creadorEntity.getId(), creador.getId());
        assertEquals(creadorEntity.getNombre(), creador.getNombre());
        assertEquals(creadorEntity.getNacionalidad(), creador.getNacionalidad());
        assertEquals(creadorEntity.getImagen(), creador.getImagen());
	}

    @Test
	void testGetInvalidCnacion()  {
		assertThrows(EntityNotFoundException.class, ()->{
			albumCreadorService.getCreador(album.getId(), 0L);
		});
	}

    	@Test
	void testGetCreadorInvalidAlbum() {
		assertThrows(EntityNotFoundException.class, ()->{
			CreadorEntity creadorEntity = creadorList.iterator().next();
			albumCreadorService.getCreador(0L, creadorEntity.getId());
		});
	}

    @Test
	void testGetNotAssociatedCreador() {
		assertThrows(IllegalOperationException.class, ()->{
			AlbumEntity newAlbum = factory.manufacturePojo(AlbumEntity.class);
			entityManager.persist(newAlbum);
			CreadorEntity creador = factory.manufacturePojo(CreadorEntity.class);
			entityManager.persist(creador);
			albumCreadorService.getCreador(newAlbum.getId(), creador.getId());
		});
	}

    @Test
	void testReplaceArtistas() throws EntityNotFoundException, IllegalOperationException {
		Set<CreadorEntity> nuevaLista = new LinkedHashSet<>();
		for (int i = 0; i < 3; i++) {
			CreadorEntity entity = factory.manufacturePojo(CreadorEntity.class);
			entityManager.persist(entity);
			album.getArtistas().add(entity);
			nuevaLista.add(entity);
		}
		albumCreadorService.replaceArtistas(album.getId(), nuevaLista);

		Set<CreadorEntity> creadorEntities = albumCreadorService.getArtistas(album.getId());
		for (CreadorEntity aNuevaLista : nuevaLista) {
			assertTrue(creadorEntities.contains(aNuevaLista));
		}
	}

    @Test
	void testReplaceArtistas2() throws EntityNotFoundException, IllegalOperationException {
		Set<CreadorEntity> nuevaLista = new LinkedHashSet<>();
		for (int i = 0; i < 3; i++) {
			CreadorEntity entity = factory.manufacturePojo(CreadorEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		albumCreadorService.replaceArtistas(album.getId(), nuevaLista);


        Set<CreadorEntity> creadorEntities = albumCreadorService.getArtistas(album.getId());
		for (CreadorEntity aNuevaLista : nuevaLista) {
			assertTrue(creadorEntities.contains(aNuevaLista));
		}
	}

    @Test
	void testReplaceArtistasInvalidAlbum(){
		assertThrows(EntityNotFoundException.class, ()->{
			Set<CreadorEntity> nuevaLista = new LinkedHashSet<>();
			for (int i = 0; i < 3; i++) {
				CreadorEntity entity = factory.manufacturePojo(CreadorEntity.class);
				entity.getAlbumes().add(album);
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			albumCreadorService.replaceArtistas(0L, nuevaLista);
		});
	}

    @Test
	void testReplaceInvalidArtistas() {
		assertThrows(EntityNotFoundException.class, ()->{
			Set<CreadorEntity> nuevaLista = new LinkedHashSet<>();
			CreadorEntity entity = factory.manufacturePojo(CreadorEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			albumCreadorService.replaceArtistas(0L, nuevaLista);
		});
	}

    @Test
	void testReplaceArtistasInvalidCreador(){
		assertThrows(EntityNotFoundException.class, ()->{
			Set<CreadorEntity> nuevaLista = new LinkedHashSet<>();
			for (int i = 0; i < 3; i++) {
				CreadorEntity entity = factory.manufacturePojo(CreadorEntity.class);
				entity.getAlbumes().add(album);
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			albumCreadorService.replaceArtistas(0L, nuevaLista);
		});
	}

	@Test
	void testReplaceInvalidCreador() {
		assertThrows(EntityNotFoundException.class, ()-> {
            Set<CreadorEntity> nuevaLista = new LinkedHashSet<>();
			CreadorEntity entity = factory.manufacturePojo(CreadorEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			albumCreadorService.replaceArtistas(album.getId(), nuevaLista);
			
        });
	}

    @Test
	void testRemoveCreador() throws EntityNotFoundException {
		for (CreadorEntity creador : creadorList) {
			albumCreadorService.removeCreador(album.getId(), creador.getId());
		}
		assertTrue(albumCreadorService.getArtistas(album.getId()).isEmpty());
	}

    @Test
	void testRemoveInvalidCreador(){
		assertThrows(EntityNotFoundException.class, ()->{
			albumCreadorService.removeCreador(album.getId(), 0L);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemoveCreadorInvalidAlbum(){
		assertThrows(EntityNotFoundException.class, ()->{
			albumCreadorService.removeCreador(0L, creadorList.iterator().next().getId());
		});
	}
}
