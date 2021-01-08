
package com.hadiai.controller;

import com.hadiai.model.Section;
import com.hadiai.model.User;
import com.hadiai.repository.SectionRepository;
import com.hadiai.repository.UserRepository;
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
	UserRepository userRepository;

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

	@GetMapping(value = "/sections/token/{token}/join")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Section> joinSection(@PathVariable("token") String token) {
		try {
			User user = jwtUtils.getUserFromJWT(); // Get current user (student);

			if (user == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			} // check if the user exists

			Optional<Section> sectionData = sectionRepository.findFirstByToken(token); // Find section by token (using
																						// optional to avoid exceptions)

			if (!sectionData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			Section section = sectionRepository.getFirstByToken(token); // Find section by token

			Set<User> students = section.getStudents();
			if (students.contains(user)) { // Check if user is already in the section
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			Set<User> requests = section.getRequests();
			if (requests.contains(user)) { // Check if user has already sent a request
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			requests.add(user); // Else add user to section's requests
			section.setRequests(requests);

			return new ResponseEntity<>(sectionRepository.save(section), HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/sections/{id}/join/{type}/user/{userId}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> joinSectionRequestAction(@PathVariable("type") String action,
			@PathVariable("id") Long sectionId, @PathVariable("userId") Long userId) {
		try {
			Long teacherId = jwtUtils.getUserIdFromJWT(); // Get current teacher ID to retrieve his sections

			if (teacherId == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			if (teacherId == userId) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}

			// Using Optional to avoid exceptions
			Optional<Section> _sectionData = sectionRepository.findByIdAndTeacher_Id(sectionId, teacherId);

			if (!_sectionData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			Optional<User> studentData = userRepository.findById(userId); // Using Optional to avoid exceptions

			if (!studentData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			User student = userRepository.getById(userId); // Get student

			Section section = sectionRepository.getByIdAndTeacher_Id(sectionId, teacherId); // Get section

			switch (action) {
			case "approve":
				Set<User> students = section.getStudents();
				students.add(student);
				section.setStudents(students); // Add user to section
				break;
			case "reject":

				break;

			default:
				new RuntimeException("Invalid action parameter");

				break;
			}

			Set<User> requests = section.getRequests();
			requests.removeIf(user -> user.getId() == userId);
			section.setRequests(requests); // Remove user from requests

			return new ResponseEntity<>(sectionRepository.save(section), HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/sections/{id}/user/{userId}/remove")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Section> removeUserFromSection(@PathVariable("id") Long sectionId,
			@PathVariable("userId") Long userId) {
		try {
			Long teacherId = jwtUtils.getUserIdFromJWT(); // Get current teacher ID to retrieve his section

			if (teacherId == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			} // check if the user exists

			// Find section by teacher id and section id
			Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(sectionId, teacherId);

			if (!sectionData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			// Get section by teacher id and section id
			Section section = sectionRepository.getByIdAndTeacher_Id(sectionId, teacherId);

			Optional<User> studentData = userRepository.findById(userId);

			if (!studentData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			User student = userRepository.getById(userId);

			Set<User> students = section.getStudents();
			if (!students.contains(student)) { // Check if user is in the section
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			// Else remove user from section
			students.removeIf(user -> user.getId() == student.getId());
			section.setStudents(students);

			return new ResponseEntity<>(sectionRepository.save(section), HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/sections/{id}/quit")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Section> quitSection(@PathVariable("id") Long sectionId) {
		try {
			User student = jwtUtils.getUserFromJWT(); // Get current user (student)

			if (student == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			} // check if the user exists

			// Find section by section id
			Optional<Section> sectionData = sectionRepository.findById(sectionId);

			if (!sectionData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			// Get section by section id
			Section section = sectionRepository.getById(sectionId);

			Set<User> students = section.getStudents();
			if (!students.contains(student)) { // Check if user is in the section
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

			// Else remove user from section
			students.removeIf(user -> user.getId() == student.getId());
			section.setStudents(students);

			return new ResponseEntity<>(sectionRepository.save(section), HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
