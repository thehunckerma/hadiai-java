package com.hadiai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hadiai.model.Section;

public interface SectionRepository extends JpaRepository<Section, Long> {
	List<Section> findByToken(String token);
	List<Section> findByNameContaining(String name);
}
