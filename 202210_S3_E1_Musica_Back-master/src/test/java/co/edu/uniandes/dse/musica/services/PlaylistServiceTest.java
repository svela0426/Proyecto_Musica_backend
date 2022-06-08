package co.edu.uniandes.dse.musica.services;


import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
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

import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.entities.CancionEntity;
import co.edu.uniandes.dse.musica.entities.PlaylistEntity;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;




@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PlaylistService.class)
class PlaylistServiceTest {
	
	@Autowired

	private PlaylistService playlistService;

	@Autowired

	private TestEntityManager entityManager;
	
	private PodamFactory factory = new PodamFactoryImpl();
	
	private Set<CancionEntity> cancionList = new LinkedHashSet<>();
	
	private Set<PlaylistEntity> playlistList = new LinkedHashSet<>();

	@BeforeEach
	void setUp() throws Exception {
		
		clearData();
		insertData();
	}

	

	@Test
	void testGetPlaylists() {
		List<PlaylistEntity> list = playlistService.getPlaylists();
		assertEquals(list.size(), playlistList.size());

		for (PlaylistEntity entity : list) {
			System.out.print(entity);
			boolean found = false;
			for (PlaylistEntity storedEntity : playlistList) {
				if (entity.getId().equals(storedEntity.getId()))
					found = true;
			}
			assertTrue(found);
		}
	}

	private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PlaylistEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from CancionEntity").executeUpdate();
}
	
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			CancionEntity cancionEntity = factory.manufacturePojo(CancionEntity.class);
			entityManager.persist(cancionEntity);
			cancionList.add(cancionEntity);
		}
		
		for (int i = 0; i < 3; i++) {
			PlaylistEntity playlistEntity = factory.manufacturePojo(PlaylistEntity.class);
			entityManager.persist(playlistEntity);
			playlistEntity.setCanciones(cancionList);
			playlistList.add(playlistEntity);
		}

}
	
	@Test
	void testCreatePlaylist() throws IllegalOperationException {
		PlaylistEntity newEntity = factory.manufacturePojo(PlaylistEntity.class);
		
		newEntity.setCanciones(cancionList);
	
		PlaylistEntity result = playlistService.createPlaylist(newEntity);
		

		PlaylistEntity entity = entityManager.find(PlaylistEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getNombre(), entity.getNombre());
		assertEquals(newEntity.getImagen(), entity.getImagen());
		assertEquals(newEntity.getFechaCreacion(), entity.getFechaCreacion());
		

	}
	@Test
	void testGetPlaylist() throws EntityNotFoundException {
		PlaylistEntity entity = playlistList.iterator().next();
		PlaylistEntity resultEntity = playlistService.getPlaylist(entity.getId());
		assertNotNull(resultEntity);

		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getNombre(), resultEntity.getNombre());
		assertEquals(entity.getImagen(), resultEntity.getImagen());
		assertEquals(entity.getFechaCreacion(), resultEntity.getFechaCreacion());
		assertEquals(entity.getCanciones(), resultEntity.getCanciones());
	}
	
	@Test
	void testDeletePlaylist() throws EntityNotFoundException, IllegalOperationException {

		PlaylistEntity entity = new ArrayList <PlaylistEntity>(playlistList).get(1);
		playlistService.deletePlaylist(entity.getId());

		PlaylistEntity deletedEntity = entityManager.find(PlaylistEntity.class, entity.getId());

		assertNull(deletedEntity);

	}

	
}