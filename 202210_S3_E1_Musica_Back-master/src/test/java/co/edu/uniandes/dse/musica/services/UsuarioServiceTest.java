package co.edu.uniandes.dse.musica.services;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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




import co.edu.uniandes.dse.musica.entities.UsuarioEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;




@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional

@Import(UsuarioService.class)
class UsuarioServiceTest {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<UsuarioEntity> usuarioList = new ArrayList<>();
	
	/**
	 * Initial test configuration.
	 */
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}


	
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from UsuarioEntity").executeUpdate();
	}

	/**
	 * Insert initial data for test.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			UsuarioEntity usuarioEntity = factory.manufacturePojo(UsuarioEntity.class);
			entityManager.persist(usuarioEntity);
			usuarioList.add(usuarioEntity);
		}
	}
	
	/**
	 * Test to create Usuario.
	 * 
	 * @throws IllegalOperationException
	 */
	
	@Test
	void testCreateUsuario() throws IllegalOperationException 
	{
		UsuarioEntity newEntity = factory.manufacturePojo(UsuarioEntity.class);
		
		UsuarioEntity result = usuarioService.createUsuario(newEntity);
		
		assertNotNull(result);
		
		UsuarioEntity entity = entityManager.find(UsuarioEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getNombre(), entity.getNombre());
		assertEquals(newEntity.getLogin(), entity.getLogin());
		assertEquals(newEntity.getCorreo(), entity.getCorreo());
		assertEquals(newEntity.getPlaylists(), entity.getPlaylists());	


	}
	
	@Test
	void testGetUsuarios() {

		List<UsuarioEntity> list = usuarioService.getUsuarios();
		assertEquals(list.size(), usuarioList.size());

		for (UsuarioEntity entity : list) {
			boolean found = false;
			for (UsuarioEntity storedEntity : usuarioList) {
				if (entity.getId().equals(storedEntity.getId()))
					found = true;
			}
			assertTrue(found);
		}

	}
	
	@Test
	void testGetUsuario() throws EntityNotFoundException {
		UsuarioEntity entity = usuarioList.get(0);
		UsuarioEntity resultEntity = usuarioService.getUsuario(entity.getId());
		assertNotNull(resultEntity);

		assertEquals(entity.getId(), entity.getId());
		assertEquals(entity.getNombre(), entity.getNombre());
		assertEquals(entity.getLogin(), entity.getLogin());
		assertEquals(entity.getCorreo(), entity.getCorreo());
		assertEquals(entity.getPlaylists(), entity.getPlaylists());



	}
	
	
	@Test
	void testUpdateUsuario() throws EntityNotFoundException, IllegalOperationException {
		UsuarioEntity entity = usuarioList.get(0);
		UsuarioEntity pojoEntity = factory.manufacturePojo(UsuarioEntity.class);
		pojoEntity.setId(entity.getId());
		usuarioService.updateUsuario(entity.getId(), pojoEntity);
		UsuarioEntity resultEntity = entityManager.find(UsuarioEntity.class, entity.getId());

		assertEquals(pojoEntity.getId(), resultEntity.getId());
		assertEquals(pojoEntity.getNombre(), resultEntity.getNombre());
		assertEquals(pojoEntity.getCorreo(), resultEntity.getCorreo());
		assertEquals(pojoEntity.getLogin(), resultEntity.getLogin());
		assertEquals(pojoEntity.getCorreo(), resultEntity.getCorreo());





	}
	
	@Test
	void testDeleteUsuario() throws EntityNotFoundException, IllegalOperationException {

		UsuarioEntity enity = usuarioList.get(1);
		usuarioService.deleteUsuario(enity.getId());

		UsuarioEntity deletedEntity = entityManager.find(UsuarioEntity.class, enity.getId());

		assertNull(deletedEntity);

	}
	
	@Test
	void testgetPlaylist() throws EntityNotFoundException, IllegalOperationException
	{
		
		UsuarioEntity entity = usuarioList.get(0);
		UsuarioEntity resultEntity = usuarioService.getUsuario(entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getPlaylists(), entity.getPlaylists());

	
	}


	
	
	
	
	
	
	
	

}
