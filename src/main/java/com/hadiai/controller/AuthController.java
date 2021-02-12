
package com.hadiai.controller;

import com.hadiai.model.ERole;
import com.hadiai.model.Role;
import com.hadiai.model.User;
import com.hadiai.payload.request.LoginRequest;
import com.hadiai.payload.request.SignupRequest;
import com.hadiai.payload.response.JwtResponse;
import com.hadiai.payload.response.MessageResponse;
import com.hadiai.repository.RoleRepository;
import com.hadiai.repository.UserRepository;
import com.hadiai.security.jwt.JwtUtils;
import com.hadiai.security.services.UserDetailsImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.Valid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
	}

	@GetMapping("/signup/validate")
	public ResponseEntity<?> validate(@RequestParam(required = true) String username,@RequestParam(required = true) String email) {
		if (userRepository.existsByUsername(username)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(email)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		return ResponseEntity.ok(true);
	}

	@PostMapping("/signup/{role}")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest,
			@PathVariable("role") String role) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getImage(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<Role> roles = new HashSet<>();
		Role userRole;

		switch (role) {
		case "user":
			userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
			break;
		case "moderator":
			userRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
			break;

		default:
			new RuntimeException("Error: Role is not found.");

			break;
		}

		// if (strRoles == null) {

		// } else {
		// strRoles.forEach(role -> {
		// switch (role) {
		// case "user":
		// Role userRole = roleRepository.findByName(ERole.ROLE_USER)
		// .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		// roles.add(userRole);
		// break;
		// case "moderator":
		// Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
		// .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		// roles.add(modRole);
		// break;
		// case "admin":
		// Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
		// .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
		// roles.add(adminRole);
		// break;
		// default:
		// new RuntimeException("Error: Role is not found.");
		// break;

		// }
		// });
		// }

		user.setRoles(roles);
		userRepository.save(user);

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signUpRequest.getUsername(), signUpRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> _roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(
				new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), _roles));
	}
}