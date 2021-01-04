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

import com.hadiai.model.Group;
import com.hadiai.model.User;
import com.hadiai.repository.GroupRepository;
import com.hadiai.security.jwt.JwtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class GroupController {

	@Autowired
	GroupRepository groupRepository;

    @Autowired
	JwtUtils jwtUtils;
	
    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

	@GetMapping("/groups")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<List<Group>> getAllGroups(@RequestParam(required = false) String name) {
		try {
			List<Group> groups = new ArrayList<Group>();

			if (name == null)
				groupRepository.findAll().forEach(groups::add);
			else
				groupRepository.findByNameContaining(name).forEach(groups::add);

			if (groups.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(groups, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/groups/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Group> getGroupById(@PathVariable("id") long id) {
		Optional<Group> groupData = groupRepository.findById(id);

		if (groupData.isPresent()) {
			return new ResponseEntity<>(groupData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/groups")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Group> createGroup(@RequestBody Group group) {
		try {
			User user = jwtUtils.getUserFromJWT();
			Set<User> users = new HashSet<User>(){{
				add(user);
			}};
			logger.info("Users : " + users);
			Group _group = groupRepository.save(new Group(group.getName(), "", users));
			return new ResponseEntity<>(_group, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/groups/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<Group> updateGroup(@PathVariable("id") long id, @RequestBody Group group) {
		Optional<Group> groupData = groupRepository.findById(id);

		if (groupData.isPresent()) {
			Group _group = groupData.get();
			_group.setName(group.getName());
			return new ResponseEntity<>(groupRepository.save(_group), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/groups/{id}")
	@PreAuthorize("hasRole('MODERATOR')")
	public ResponseEntity<HttpStatus> deleteGroup(@PathVariable("id") long id) {
		try {
			groupRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
