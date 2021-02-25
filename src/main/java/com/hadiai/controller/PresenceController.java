
package com.hadiai.controller;

import com.hadiai.model.Presence;
import com.hadiai.model.Section;
import com.hadiai.model.Session;
import com.hadiai.model.User;
import com.hadiai.repository.PresenceRepository;
import com.hadiai.repository.SectionRepository;
import com.hadiai.repository.SessionRepository;
import com.hadiai.repository.UserRepository;

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
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class PresenceController {

	@Autowired
	PresenceRepository presenceRepository;

	@Autowired
	SessionRepository sessionRepository;

	@Autowired
	SectionRepository sectionRepository;

	@Autowired
	UserRepository userRepository;

	@GetMapping("/presence/{sessionId}/{userId}/raw")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<Presence>> getPresence(@PathVariable("sessionId") long sessionId,
			@PathVariable("userId") long userId) {
		List<Presence> presenceData = presenceRepository.findBySession_IdAndStudent_Id(sessionId, userId);

		if (!presenceData.isEmpty()) {
			return new ResponseEntity<>(presenceData, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/presence/{sessionId}/{userId}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> getPresenceByUser(@PathVariable("sessionId") long sessionId,
			@PathVariable("userId") long userId) {

		Optional<Session> sessionData = sessionRepository.findById(sessionId);

		if (sessionData.isPresent()) {
			Session session = sessionData.get();
			List<Presence> presenceList = presenceRepository.findBySession_IdAndStudent_Id(sessionId, userId);

			if (presenceList.isEmpty()) {
				return new ResponseEntity<>(0, HttpStatus.OK);
			} else {
				Date startDate = session.getCreatedDate();
				Date endDate = new Date();
				if (session.getEnd()) {
					endDate = session.getLastModifiedDate();
				}

				long sessionDuration = (endDate.getTime() - startDate.getTime()) / 1000; // in seconds

				long presenceDuration = 0;
				Date currentPresenceDate = presenceList.get(0).getCreatedDate();
				Date startCountPresenceDate = currentPresenceDate;
				Date endCountPresenceDate = currentPresenceDate;
				Date previousPresenceDate = currentPresenceDate;
				long len = presenceList.size() - 1;
				long count = 0;
				for (Presence presence : presenceList) {
					count++;
					currentPresenceDate = presence.getCreatedDate();
					long duration = (currentPresenceDate.getTime() - previousPresenceDate.getTime()) / 1000; // in
																												// seconds
					if (duration > 5 || count >= len) {
						// Increment presence duration by counted duration and reset other vairables to
						// start a new counting
						presenceDuration += (endCountPresenceDate.getTime() - startCountPresenceDate.getTime()) / 1000; // in
																														// seconds
						startCountPresenceDate = currentPresenceDate;
					}
					endCountPresenceDate = currentPresenceDate;
					previousPresenceDate = currentPresenceDate;
				}

				float presencePercentage = ((float) presenceDuration / (float) sessionDuration) * 100;
				return new ResponseEntity<>(presencePercentage, HttpStatus.OK);
			}
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/presence/{sessionId}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> getPresence(@PathVariable("sessionId") long sessionId) {

		Optional<Session> sessionData = sessionRepository.findById(sessionId);

		if (sessionData.isPresent()) {
			Session session = sessionData.get();
			Section section = session.getSection();
			Set<User> users = section.getStudents();
			List<UserPresence> userPresences = new ArrayList<UserPresence>();

			if (!users.isEmpty()) {
				for (User user : users) {
					List<Presence> presenceList = presenceRepository.findBySession_IdAndStudent_Id(sessionId,
							user.getId());

					if (presenceList.isEmpty()) {
						return new ResponseEntity<>(0, HttpStatus.OK);
					} else {
						Date startDate = session.getCreatedDate();
						Date endDate = new Date();
						if (session.getEnd()) {
							endDate = session.getLastModifiedDate();
						}

						long sessionDuration = (endDate.getTime() - startDate.getTime()) / 1000; // in seconds

						long presenceDuration = 0;
						Date currentPresenceDate = presenceList.get(0).getCreatedDate();
						Date startCountPresenceDate = currentPresenceDate;
						Date endCountPresenceDate = currentPresenceDate;
						Date previousPresenceDate = currentPresenceDate;
						long len = presenceList.size() - 1;
						long count = 0;
						for (Presence presence : presenceList) {
							count++;
							currentPresenceDate = presence.getCreatedDate();
							long duration = (currentPresenceDate.getTime() - previousPresenceDate.getTime()) / 1000; // in
																														// seconds
							if (duration > 5 || count >= len) {
								// Increment presence duration by counted duration and reset other vairables to
								// start a new counting
								presenceDuration += (endCountPresenceDate.getTime() - startCountPresenceDate.getTime())
										/ 1000; // in
												// seconds
								startCountPresenceDate = currentPresenceDate;
							}
							endCountPresenceDate = currentPresenceDate;
							previousPresenceDate = currentPresenceDate;
						}

						float presencePercentage = ((float) presenceDuration / (float) sessionDuration) * 100;
						userPresences.add(new UserPresence(user.getId(), user.getUsername(), user.getEmail(),
								presencePercentage));
					}
				}

			}
			return new ResponseEntity<>(userPresences, HttpStatus.OK);

		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/python/presence/{sectionId}/{userId}")
	public ResponseEntity<Presence> markPresence(@PathVariable("sectionId") long sectionId,
			@PathVariable("userId") long userId) {
		try {
			Optional<Section> sectionData = sectionRepository.findById(sectionId);

			if (sectionData.isPresent()) {
				Section section = sectionRepository.getById(sectionId);
				Set<Session> sessions = section.getSessions();

				Long sessionId = null;

				for (Session session : sessions) {
					if (session.getEnd()) {
					} else {
						sessionId = session.getId();
					}
				}

				if (sessionId > 0) {

					Optional<Session> sessionData = sessionRepository.findById(sessionId);

					if (sessionData.isPresent()) {
						Session session = sessionData.get();

						Optional<User> userData = userRepository.findById(userId);

						if (userData.isPresent()) {
							User user = userData.get();

							Presence _presence = presenceRepository.save(new Presence(session, user));
							return new ResponseEntity<>(_presence, HttpStatus.CREATED);

						}
					}
				}
			}
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

class UserPresence {
	public UserPresence(long id, String username, String email, float presencePercentage) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.presencePercentage = presencePercentage;
	}

	private long id;
	private String username;
	private String email;
	private float presencePercentage;

	public long getId() {
		return id;
	}

	public void setEmail(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public float getPresencePercentage() {
		return presencePercentage;
	}

	public void setPresencePercentage(float presencePercentage) {
		this.presencePercentage = presencePercentage;
	}

}