
package com.hadiai.controller;

import com.hadiai.model.Section;
import com.hadiai.model.Session;
import com.hadiai.repository.SectionRepository;
import com.hadiai.repository.SessionRepository;
import com.hadiai.repository.UserRepository;
import com.hadiai.security.jwt.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class SessionController {

	@Autowired
	SessionRepository sessionRepository;

	@Autowired
	SectionRepository sectionRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtils jwtUtils;

	private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

	@GetMapping("/sessions/sections/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<List<Session>> getAllSessions(@PathVariable("id") Long id) {
		try {
			Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher Id

			if (userId == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(id, userId);

			if (!sectionData.isPresent()) { // Verify that the teacher owns the Section
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			List<Session> sessions = new ArrayList<Session>();

			// Get section sessions
			sessionRepository.getBySection_Id(id).forEach(sessions::add);

			if (sessions.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(sessions, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/sessions/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Session> getSessionById(@PathVariable("id") Long id) {
		try {

			Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher Id

			if (userId == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			Optional<Session> sessionData = sessionRepository.findById(id);

			if (!sessionData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			Session session = sessionRepository.getById(id);

			Long sectionId = session.getSection().getId(); // Get section Id

			Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(sectionId, userId);

			if (!sectionData.isPresent()) { // Verify that the teacher owns the Section
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			return new ResponseEntity<>(sessionData.get(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/sessions/sections/{id}/start")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Session> startSession(@PathVariable("id") Long id) {
		try {
			Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher Id

			if (userId == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(id, userId);

			if (!sectionData.isPresent()) { // Verify that the teacher owns the Section
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			Section section = sectionRepository.getByIdAndTeacher_Id(id, userId);

			return new ResponseEntity<>(sessionRepository.save(new Session(section)), HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/sessions/{id}/end")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Session> endSession(@PathVariable("id") Long id) {
		try {

			Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher Id

			if (userId == null) {
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			Optional<Session> sessionData = sessionRepository.findById(id);

			if (!sessionData.isPresent()) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			Session session = sessionRepository.getById(id);

			Long sectionId = session.getSection().getId(); // Get section Id

			Optional<Section> sectionData = sectionRepository.findByIdAndTeacher_Id(sectionId, userId);

			if (!sectionData.isPresent()) { // Verify that the teacher owns the Section
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			}

			session.setEnd(true);
			return new ResponseEntity<>(sessionRepository.save(session), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
