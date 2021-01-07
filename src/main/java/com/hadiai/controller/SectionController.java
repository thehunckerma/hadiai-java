
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
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class SectionController {

	@Autowired
	SectionRepository sectionRepository;

	@Autowired
	JwtUtils jwtUtils;

	private static final Logger logger = LoggerFactory.getLogger(SectionController.class);

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
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

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

	@PostMapping(value = "/sections")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> createSection(@RequestBody Section section) {
		try {
			User user = jwtUtils.getUserFromJWT(); // Get current user (teacher);

			if (user == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}
			// Create section with current user as teacher and save it, and return a 201
			// created code with the created section
			return new ResponseEntity<>(
					sectionRepository.save(new Section(section.getName(), section.getDescription(), user)),
					HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/sections/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> updateSection(@PathVariable("id") long id, @RequestBody Section section) {
		Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher ID to retrieve his section

		if (userId == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		// Retrieve section by ID (/id) and teacher ID
		Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(id, userId);

		if (sectionData.isPresent()) {
			Section _section = sectionData.get();
			_section.setName(section.getName()); // Update section's name
			_section.setDescription(section.getDescription()); // Update section's description
			return new ResponseEntity<>(sectionRepository.save(_section), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

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

	@GetMapping(value = "/sections/join/{token}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Section> joinSection(@PathVariable("token") String token) {
		try {
			User user = jwtUtils.getUserFromJWT(); // Get current user (student);

			if (user == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			} // check if the user exists

			Optional<Section> _sectionData = sectionRepository.findFirstByToken(token); // Find section by token (using
																						// optional to avoid exceptions)

			if (!_sectionData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			Section sectionData = sectionRepository.getFirstByToken(token); // Find section by token

			Set<User> requests = sectionData.getRequests();
			requests.add(user);
			sectionData.setRequests(requests); // Add user to section's requests

			return new ResponseEntity<>(sectionRepository.save(sectionData), HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
