
package com.hadiai.repository;

import com.hadiai.model.Section;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
	Section getFirstByToken(String token);

	Optional<Section> findFirstByToken(String token);

	List<Section> findByNameContainingAndTeacher_Id(String name, Long id);

	List<Section> findByTeacher_Id(Long id);

	Optional<Section> findByIdAndTeacher_Id(Long id, Long userId);

}
