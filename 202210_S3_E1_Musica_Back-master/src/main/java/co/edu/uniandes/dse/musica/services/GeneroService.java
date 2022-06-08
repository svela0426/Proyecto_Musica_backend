package co.edu.uniandes.dse.musica.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.musica.entities.GeneroEntity;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;
import co.edu.uniandes.dse.musica.repositories.GeneroRepository;

/**
 * Clase que implementa la conexion con la base de datos de los generos
 * 
 * @author juancamilobonet2
 */


@Service
public class GeneroService {
	
	@Autowired
	GeneroRepository generoRepository;
	
	
	
	
	
	@Transactional
	public GeneroEntity createGenero (GeneroEntity genero) {
		//Crea un nuevo genero y lo pone en la base de datos		
		return generoRepository.save(genero);
	}
	
	@Transactional
	public List<GeneroEntity> getGeneros() {
		//retorna todos los generos en la base de datos
		return generoRepository.findAll();
	}
	
	@Transactional
	public GeneroEntity getGenero(Long generoId) throws EntityNotFoundException {
		//retorna el genero buscado, tira excepcion si no se encuentra
		
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if(generoEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
		}
		return generoEntity.get();
	}
	
	@Transactional
	public GeneroEntity updateGenero(Long generoId, GeneroEntity generoCambiado) throws EntityNotFoundException {
		//cambia un genero en la base de datos por otro, tira excepcion si no se encuentra el id
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if(generoEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
		}
		
		generoCambiado.setId(generoId);
		
		
		return generoRepository.save(generoCambiado);
	}
	
	@Transactional
	public void deleteGenero(Long generoId) throws EntityNotFoundException {
		//borra un genero de la BD, tira excepcion si no lo encuentra
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if(generoEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
		}
		
	generoRepository.deleteById(generoId);
	}
	
	
	
}
