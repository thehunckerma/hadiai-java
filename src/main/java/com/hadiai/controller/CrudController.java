
package com.hadiai.controller;

import com.hadiai.model.Crud;
import com.hadiai.repository.CrudRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class CrudController {

	@Autowired
	CrudRepository crudRepository;

	@GetMapping("/cruds")
	public ResponseEntity<List<Crud>> getAllCruds(@RequestParam(required = false) String name) {
		try {
			List<Crud> cruds = new ArrayList<Crud>();

			if (name == null)
				crudRepository.findAll().forEach(cruds::add);
			else
				crudRepository.findByNameContaining(name).forEach(cruds::add);

			if (cruds.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(cruds, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/cruds/{id}")
	public ResponseEntity<Crud> getCrudById(@PathVariable("id") long id) {
		Optional<Crud> crudData = crudRepository.findById(id);

		if (crudData.isPresent()) {
			return new ResponseEntity<>(crudData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/cruds")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<Crud> createCrud(@RequestBody Crud crud) {
		try {
			Crud _crud = crudRepository.save(new Crud(crud.getName(), crud.getDescription(), false));
			return new ResponseEntity<>(_crud, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/cruds/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<Crud> updateCrud(@PathVariable("id") long id, @RequestBody Crud crud) {
		Optional<Crud> crudData = crudRepository.findById(id);

		if (crudData.isPresent()) {
			Crud _crud = crudData.get();
			_crud.setName(crud.getName());
			_crud.setDescription(crud.getDescription());
			_crud.setPublished(crud.isPublished());
			return new ResponseEntity<>(crudRepository.save(_crud), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/cruds/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<HttpStatus> deleteCrud(@PathVariable("id") long id) {
		try {
			crudRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/cruds")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<HttpStatus> deleteAllCruds() {
		try {
			crudRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/cruds/published")
	public ResponseEntity<List<Crud>> findByPublished() {
		try {
			List<Crud> cruds = crudRepository.findByPublished(true);

			if (cruds.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(cruds, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
