
package com.hadiai.controller;

import com.hadiai.model.Section;
import com.hadiai.model.User;
import com.hadiai.repository.SectionRepository;
import com.hadiai.security.jwt.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SectionController {

	@Autowired
	SectionRepository sectionRepository;

	@Autowired
	JwtUtils jwtUtils;

	private static final Logger logger = LoggerFactory.getLogger(SectionController.class);

	// =============================== DONE ===============================
	@GetMapping("/sections")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<List<Section>> getAllSections(@RequestParam(required = false) String name) {
		try {
			List<Section> sections = new ArrayList<Section>();

			Long id = jwtUtils.getUserIdFromJWT();// Get current teacher ID to retrieve his sections

			if (id == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			if (name == null) {
				// Retrieve all groups created by current teacher
				sectionRepository.findByTeacher_Id(id).forEach(sections::add);
			} else {
				// Retrieve all groups with name containing the name parameter (?name=...) and
				// created by current teacher
				sectionRepository.findByNameContainingAndTeacher_Id(name, id).forEach(sections::add);
			}

			if (sections.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(sections, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// =============================== DONE ===============================
	@GetMapping("/sections/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> getSectionById(@PathVariable("id") long id) {
		Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher ID to retrieve his sections

		if (userId == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(id, userId);

		if (sectionData.isPresent()) {
			return new ResponseEntity<>(sectionData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// =============================== DONE ===============================
	@PostMapping(value = "/sections")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> createSection(@RequestBody Section section) {
		try {
			User user = jwtUtils.getUserFromJWT(); // Get current user (teacher);

			if (user == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			return new ResponseEntity<>(sectionRepository.save(new Section(section.getName(), user)),
					HttpStatus.CREATED); // Create section with current user as teacher and save it,
			// and return a 201 created code with the created section
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// =============================== DONE ===============================
	@PutMapping("/sections/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> updateSection(@PathVariable("id") long id, @RequestBody Section section) {
		Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher ID to retrieve his section

		if (userId == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(id, userId); // Retrieve section by ID
																								// (/id) and teacher ID

		if (sectionData.isPresent()) {
			Section _section = sectionData.get();
			_section.setName(section.getName()); // Update section's name
			return new ResponseEntity<>(sectionRepository.save(_section), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// =============================== DONE ===============================
	@DeleteMapping("/sections/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<HttpStatus> deleteSection(@PathVariable("id") long id) {
		try {
			Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher ID to retrieve his section

			if (userId == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			// Verify that section belongs to teacher
			Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(id, userId);

			if (!sectionData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			sectionRepository.deleteById(id); // Delete section
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
