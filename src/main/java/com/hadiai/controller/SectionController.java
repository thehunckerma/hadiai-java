package com.hadiai.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.hadiai.model.Section;
import com.hadiai.model.User;
import com.hadiai.repository.SectionRepository;
import com.hadiai.security.jwt.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class SectionController {

	@Autowired
	SectionRepository sectionRepository;

    @Autowired
	JwtUtils jwtUtils;
	
    private static final Logger logger = LoggerFactory.getLogger(SectionController.class);

	// ========================= DONE ===============================
	@GetMapping("/sections")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<List<Section>> getAllSections(@RequestParam(required = false) String name) {
		try {
			List<Section> sections = new ArrayList<Section>();

			if (name == null)
				sectionRepository.findAll().forEach(sections::add);
			else
				sectionRepository.findByNameContaining(name).forEach(sections::add);

			if (sections.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(sections, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ========================= DONE ===============================
	@GetMapping("/sections/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> getSectionById(@PathVariable("id") long id) {
		Optional<Section> sectionData = sectionRepository.findById(id);

		if (sectionData.isPresent()) {
			return new ResponseEntity<>(sectionData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// ========================= DONE ===============================
	@PostMapping("/sections")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> createSection(@RequestBody Section section) {
		try {
			User user = jwtUtils.getUserFromJWT();
			Section _section = sectionRepository.save(new Section(section.getName(), "", user));
			return new ResponseEntity<>(_section, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/sections/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> updateSection(@PathVariable("id") long id, @RequestBody Section section) {
		Optional<Section> sectionData = sectionRepository.findById(id);

		if (sectionData.isPresent()) {
			Section _section = sectionData.get();
			_section.setName(section.getName());
			return new ResponseEntity<>(sectionRepository.save(_section), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/sections/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<HttpStatus> deleteSection(@PathVariable("id") long id) {
		try {
			sectionRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
