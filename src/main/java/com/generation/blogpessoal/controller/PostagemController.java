package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController // indica que a classe controller Postagem  vai receber inquisiçoes e vai responder
@RequestMapping("/postagens") // indica que as requisiçoes endpoint que começar com /Postagem serao tratadas por essa controller
@CrossOrigin(origins = "*", allowedHeaders = "*") // permite receber requisiçoes de qualquer front end
public class PostagemController {

	@Autowired // inversao de controle
	private PostagemRepository postagemRepository;
	
	@Autowired
	private TemaRepository temaRepository;
	
	@GetMapping // todas as requisiçoes do tipo get vai ser executadas por esse metodo
	public ResponseEntity<List<Postagem>> getAll(){ // get all vai buscar todas as postagens, e vai ser retornado um lista de postagens
		return ResponseEntity.ok(postagemRepository.findAll()); // aqui ele vai tar retornando a reposta, e o tipo de resposta vai ser um ok = sucesso e dentro desse sucesso vai ter todos os registros da tabela postagem atravez do metodo find all
	}
	
 
     @GetMapping("/{id}")
     public ResponseEntity<Postagem> getById(@PathVariable Long id){
	     return postagemRepository.findById(id)
			     .map(resp -> ResponseEntity.ok(resp))
			     .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
     }
     
     @GetMapping("/titulo/{titulo}")
 	public ResponseEntity<List<Postagem>> getByTitulo(@PathVariable String titulo){
 		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo));
 	}

     @PostMapping
 	 public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) {
    	 
    	 if (temaRepository.existsById(postagem.getTema().getId())) {
 		
 		postagem.setId(null);
 		
 		return ResponseEntity.status(HttpStatus.CREATED)
 				.body(postagemRepository.save(postagem));
    	 }
    	 
    	 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
 	}
     
    @PutMapping
 	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) {
    	if (postagemRepository.existsById(postagem.getId())) {
    		
    		if (temaRepository.existsById(postagem.getTema().getId()))
    			return ResponseEntity.status(HttpStatus.OK)
    					.body(postagemRepository.save(postagem));
    		
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existe!", null);
    		
    	}
    					
 		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
 	}
 	
    @ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Postagem> postagem = postagemRepository.findById(id);
		
		if(postagem.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		postagemRepository.deleteById(id);				
	}
}