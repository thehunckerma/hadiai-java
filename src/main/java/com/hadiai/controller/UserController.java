
package com.hadiai.controller;

import com.hadiai.model.User;
import com.hadiai.repository.UserRepository;
import com.hadiai.security.jwt.JwtUtils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/account")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	JwtUtils jwtUtils;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@GetMapping("")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
	public ResponseEntity<User> getUserById() {
		Long userId = jwtUtils.getUserIdFromJWT(); // Get current user ID to retrieve his data

		if (userId == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		Optional<User> userData = userRepository.findById(userId);

		if (userData.isPresent()) {
			return new ResponseEntity<>(userData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/update")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR')")
	public ResponseEntity<User> updateUser(@RequestBody User user) {

		Long userId = jwtUtils.getUserIdFromJWT(); // Get current user ID to retrieve his data

		if (userId == null) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		Optional<User> userData = userRepository.findById(userId);

		if (userData.isPresent()) {
			User _user = userRepository.getById(userId);

			String username = user.getUsername();
			if (!StringUtils.isEmpty(username)) {
				_user.setUsername(username);
			}

			String email = user.getEmail();
			if (!StringUtils.isEmpty(email)) {
				_user.setEmail(email);
			}

			String password = user.getPassword();
			if (!StringUtils.isEmpty(password)) {
				_user.setPassword(password);
			}

			String image = user.getImage();
			if (!StringUtils.isEmpty(image)) {
				_user.setImage(image);
			}

			return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}