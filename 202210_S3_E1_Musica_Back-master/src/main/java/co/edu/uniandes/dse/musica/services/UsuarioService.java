package co.edu.uniandes.dse.musica.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.musica.entities.UsuarioEntity;
import co.edu.uniandes.dse.musica.repositories.UsuarioRepository;
import co.edu.uniandes.dse.musica.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.musica.exceptions.ErrorMessage;





 /**
  * Class that manages the logic for the Usuario entity.
 * Uses UsuarioRepository to manage persistence.
 *
 * @author svela0426
  */

 @Service
public class UsuarioService {
  

	 	@Autowired
        UsuarioRepository usuarioRepository;
  

  
       
 
        @Transactional
        public UsuarioEntity createUsuario(UsuarioEntity usuario){
        	return usuarioRepository.save(usuario);
          }
 
  

        @Transactional
        public List<UsuarioEntity> getUsuarios(){
                return usuarioRepository.findAll();
          }
          
          
          
          
  
 
 
          @Transactional
          public UsuarioEntity getUsuario(Long authorid)  throws EntityNotFoundException 
          {
        	  Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(authorid);
        		if (usuarioEntity.isEmpty())
        			throw new EntityNotFoundException (ErrorMessage.USUARIO_NOT_FOUND);
        		return usuarioEntity.get();
          }

 

          @Transactional
          public void deleteUsuario(Long id) throws EntityNotFoundException 
          {
      		Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(id);

      		if (usuarioEntity.isEmpty())
    			throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);
      		


      		
      		usuarioRepository.deleteById(id);
      		
          }
      		
      			
    			      		

    		
         
    		
          
 
          @Transactional
          public UsuarioEntity updateUsuario(Long id, UsuarioEntity usuario) throws EntityNotFoundException 
          {
      		Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(id);
      		if (usuarioEntity.isEmpty())
      			throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);
      		usuario.setId(id);
      		return usuarioRepository.save(usuario);
          }
          
          
          
 

 }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
          
 
 
 
