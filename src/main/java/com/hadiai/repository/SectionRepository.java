
package com.hadiai.repository;

import com.hadiai.model.Section;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
	List<Section> findByToken(String token);

	List<Section> findByNameContainingAndTeacher_Id(String name, Long id);

	List<Section> findByTeacher_Id(Long id);
}
