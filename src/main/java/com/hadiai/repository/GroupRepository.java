package com.hadiai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hadiai.model.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
	List<Group> findByToken(String token);
	List<Group> findByNameContaining(String name);
}
