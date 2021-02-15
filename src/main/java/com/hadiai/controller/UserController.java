
package com.hadiai.controller;

import com.hadiai.model.User;
import com.hadiai.repository.UserRepository;
import com.hadiai.security.jwt.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtils jwtUtils;

	// private static final Logger logger =
	// LoggerFactory.getLogger(UserController.class);

	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
		Optional<User> userData = userRepository.findById(id);

		if (userData.isPresent()) {
			return new ResponseEntity<>(userData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
	public ResponseEntity<User> updateUser(@PathVariable("id") long id, @RequestBody User user) {

		// Long userId = jwtUtils.getUserIdFromJWT(); // Get current teacher ID to
		// retrieve his data

		// if (userId == null) {
		// return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		// }

		Optional<User> userData = userRepository.findById(id);

		if (userData.isPresent()) {
			User _user = userRepository.getById(id);
			_user.setUsername(user.getUsername());
			_user.setEmail(user.getEmail());
			_user.setImage(user.getImage());
			_user.setPassword(user.getPassword());
			return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}